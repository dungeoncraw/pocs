mod db;
mod github;
mod grader;
mod cli;

use clap::Parser;
use cli::Cli;
use anyhow::{Context, Result};
use chrono::{DateTime, Utc, NaiveDate};
use dotenvy::dotenv;
use tracing::{info, error};
use tracing_subscriber;
use dialoguer::{Select, Input, theme::ColorfulTheme};

#[tokio::main]
async fn main() -> Result<()> {
    dotenv().ok();
    tracing_subscriber::fmt::init();

    let mut cli = Cli::parse();
    let pool = db::init_db(&cli.db).await.context("Failed to initialize database")?;

    // Interactive Mode - ensure tokens are present
    ensure_tokens(&mut cli)?;
    interactive_mode(&pool, &cli).await?;

    Ok(())
}

fn ensure_tokens(cli: &mut Cli) -> Result<()> {
    if cli.github_token.is_none() {
        let token: String = Input::with_theme(&ColorfulTheme::default())
            .with_prompt("GITHUB_TOKEN is missing. Please enter it")
            .interact_text()?;
        cli.github_token = Some(token);
    }

    if cli.openai_api_key.is_none() {
        let key: String = Input::with_theme(&ColorfulTheme::default())
            .with_prompt("OPENAI_API_KEY is missing. Please enter it")
            .interact_text()?;
        cli.openai_api_key = Some(key);
    }

    // Optionally save to .env if it doesn't exist or doesn't have these
    if !std::path::Path::new(".env").exists() {
        if Select::with_theme(&ColorfulTheme::default())
            .with_prompt("Would you like to save these tokens to a .env file?")
            .items(&["Yes", "No"])
            .default(0)
            .interact()? == 0 {
            let content = format!(
                "GITHUB_TOKEN={}\nOPENAI_API_KEY={}\n",
                cli.github_token.as_ref().unwrap(),
                cli.openai_api_key.as_ref().unwrap()
            );
            std::fs::write(".env", content)?;
            println!(".env file created!");
        }
    }

    Ok(())
}

async fn run_grading(
    pool: &sqlx::SqlitePool,
    cli: &Cli,
    user: String,
    since: Option<String>,
    today: bool,
    max: usize,
) -> Result<()> {
    let github_token = cli.github_token.clone().context("GITHUB_TOKEN is required for 'run'")?;
    let openai_api_key = cli.openai_api_key.clone().context("OPENAI_API_KEY is required for 'run'")?;

    let gh_client = github::GithubClient::new(Some(github_token))?;
    let model_name = cli.openai_model.clone();
    let grader = grader::Grader::new(openai_api_key, Some(cli.openai_model.clone()));

    let since_date = if today {
        Some(Utc::now().date_naive().and_hms_opt(0, 0, 0).unwrap().and_local_timezone(Utc).unwrap())
    } else if let Some(s) = since {
        Some(DateTime::<Utc>::from_naive_utc_and_offset(
            NaiveDate::parse_from_str(&s, "%Y-%m-%d")?.and_hms_opt(0, 0, 0).unwrap(),
            Utc,
        ))
    } else {
        None
    };

    let mut memory = db::get_memory(pool, &user).await?;

    info!("Fetching public repositories for user: {}", user);
    let public_repos = gh_client.list_user_repos(&user).await?;
    
    if public_repos.is_empty() {
        println!("\n⚠️ No public repositories found for user {}.", user);
        return Ok(());
    }

    for gh_repo in public_repos {
        info!("Processing repo: {}/{}", gh_repo.owner.login, gh_repo.name);
        
        // Ensure repo is in DB
        let repo_id = db::add_repo(pool, &gh_repo.owner.login, &gh_repo.name, &gh_repo.default_branch).await?;
        
        let last_sha = db::get_repo_state(pool, repo_id).await?;
        
        let commits = match gh_client.list_commits(&gh_repo.owner.login, &gh_repo.name, &user, since_date).await {
            Ok(c) => c,
            Err(e) => {
                error!("Failed to list commits for {}/{}: {}", gh_repo.owner.login, gh_repo.name, e);
                continue;
            }
        };
        
        let mut commits_to_process = Vec::new();
        for commit in commits {
            if let Some(ref ls) = last_sha {
                if commit.sha == *ls {
                    break;
                }
            }
            commits_to_process.push(commit);
            if commits_to_process.len() >= max {
                break;
            }
        }
        
        commits_to_process.reverse();

        for commit in commits_to_process {
            // Check if commit was already graded (idempotency)
            if db::is_commit_graded(pool, repo_id, &commit.sha).await? {
                info!("Commit {} already graded, skipping", &commit.sha[..7]);
                continue;
            }

            info!("Grading commit: {}", &commit.sha[..7]);
            match gh_client.get_commit_details(&gh_repo.owner.login, &gh_repo.name, &commit.sha).await {
                Ok(details) => {
                    match grader.grade_commit(&details, memory.as_deref()).await {
                        Ok(grading) => {
                            println!("✅ Graded commit {} in {}/{}: {}", &commit.sha[..7], gh_repo.owner.login, gh_repo.name, grading.grade);
                            if let Some(ref new_mem) = grading.updated_memory {
                                db::update_memory(pool, &user, new_mem).await?;
                                memory = Some(new_mem.clone());
                            }

                            let review = db::CommitReview {
                                repo_id,
                                sha: commit.sha.clone(),
                                author_login: user.clone(),
                                committed_at: commit.commit.author.date,
                                message: commit.commit.message.clone(),
                                stats_additions: details.stats.additions,
                                stats_deletions: details.stats.deletions,
                                files_changed: details.files.len() as i32,
                                grade: grading.grade,
                                rationale: grading.summary,
                                model: model_name.clone(),
                                created_at: Utc::now(),
                            };
                            db::save_review(pool, &review).await?;
                            db::update_repo_state(pool, repo_id, &commit.sha).await?;
                            info!("Grade: {}", review.grade);
                        }
                        Err(e) => error!("Failed to grade commit {}: {}", commit.sha, e),
                    }
                }
                Err(e) => error!("Failed to fetch details for {}: {}", commit.sha, e),
            }
        }
    }
    Ok(())
}

async fn show_report(pool: &sqlx::SqlitePool) -> Result<()> {
    let reviews = db::get_recent_reviews(pool, 100).await?;
    println!("{:<10} | {:<7} | {:<5} | {}", "Date", "SHA", "Grade", "Summary");
    println!("{:-<10}-+-{:-<7}-+-{:-<5}-+-----------------", "", "", "");
    for r in reviews {
        println!("{:<10} | {:<7} | {:<5} | {}", 
            r.committed_at.format("%Y-%m-%d"), 
            &r.sha[..7], 
            r.grade, 
            r.rationale
        );
    }

    let memories = db::get_all_memories(pool).await?;
    if !memories.is_empty() {
        println!("\n{:<15} | {}", "User", "Memory/Longitudinal Coaching");
        println!("{:-<15}-+-----------------------------------", "");
        for (user, mem) in memories {
            println!("{:<15} | {}", user, mem);
        }
    }
    Ok(())
}

async fn interactive_mode(pool: &sqlx::SqlitePool, cli: &Cli) -> Result<()> {
    println!("Welcome to Raquel Agent Interactive Mode!");

    loop {
        let users = db::get_tracked_users(pool).await?;
        let main_options = vec!["Grade a user".to_string(), "Exit".to_string()];
        
        let main_selection = Select::with_theme(&ColorfulTheme::default())
            .with_prompt("Main Menu")
            .items(&main_options)
            .default(0)
            .interact()?;

        match main_selection {
            0 => {
                // Grade a user
                let mut users_options = users.clone();
                users_options.push("Add a new user".to_string());
                users_options.push("Back to main menu".to_string());

                let selection = Select::with_theme(&ColorfulTheme::default())
                    .with_prompt("Select a user to grade")
                    .items(&users_options)
                    .default(0)
                    .interact()?;

                if selection == users_options.len() - 1 {
                    continue;
                }

                let user = if selection == users_options.len() - 2 {
                    let input: String = Input::with_theme(&ColorfulTheme::default())
                        .with_prompt("Enter GitHub username")
                        .interact_text()?;
                    db::ensure_user(pool, &input).await?;
                    input
                } else {
                    users[selection].clone()
                };

                let options = vec!["Today's commits", "Last 50 commits (idempotent)", "Specific date", "Back to main menu"];
                let period_selection = Select::with_theme(&ColorfulTheme::default())
                    .with_prompt(format!("Which commits should I grade for {}?", user))
                    .items(&options)
                    .default(0)
                    .interact()?;

                if period_selection == 3 {
                    continue;
                }

                let (today, since) = match period_selection {
                    0 => (true, None),
                    1 => (false, None),
                    2 => {
                        let date: String = Input::with_theme(&ColorfulTheme::default())
                            .with_prompt("Enter start date (YYYY-MM-DD)")
                            .interact_text()?;
                        (false, Some(date))
                    },
                    _ => unreachable!(),
                };

                if let Err(e) = run_grading(pool, cli, user, since, today, 50).await {
                    error!("Error during grading: {}", e);
                }
                
                println!("\nGrading complete. Would you like to see the report?");
                if Select::with_theme(&ColorfulTheme::default())
                    .items(&["Yes", "No"])
                    .default(0)
                    .interact()? == 0 {
                    if let Err(e) = show_report(pool).await {
                        error!("Error showing report: {}", e);
                    }
                }
            }
            1 => {
                // Exit
                break;
            }
            _ => unreachable!(),
        }
        println!("\n---\n");
    }

    println!("Goodbye!");
    Ok(())
}
