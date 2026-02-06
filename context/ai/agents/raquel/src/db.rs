use sqlx::{sqlite::SqlitePoolOptions, SqlitePool, FromRow};
use anyhow::Result;
use chrono::{DateTime, Utc};

#[derive(FromRow)]
pub struct Repo {
    pub id: i64,
}

#[derive(FromRow)]
pub struct CommitReview {
    pub repo_id: i64,
    pub sha: String,
    pub author_login: String,
    pub committed_at: DateTime<Utc>,
    pub message: String,
    pub stats_additions: i32,
    pub stats_deletions: i32,
    pub files_changed: i32,
    pub grade: String,
    pub rationale: String,
    pub model: String,
    pub created_at: DateTime<Utc>,
}

pub async fn init_db(db_url: &str) -> Result<SqlitePool> {
    let pool = SqlitePoolOptions::new()
        .connect(db_url)
        .await?;

    sqlx::query(
        "CREATE TABLE IF NOT EXISTS repos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            owner TEXT NOT NULL,
            name TEXT NOT NULL,
            default_branch TEXT NOT NULL,
            UNIQUE(owner, name)
        )"
    ).execute(&pool).await?;

    sqlx::query(
        "CREATE TABLE IF NOT EXISTS repo_state (
            repo_id INTEGER PRIMARY KEY,
            last_seen_sha TEXT,
            last_run_at DATETIME,
            FOREIGN KEY(repo_id) REFERENCES repos(id)
        )"
    ).execute(&pool).await?;

    sqlx::query(
        "CREATE TABLE IF NOT EXISTS commit_reviews (
            repo_id INTEGER,
            sha TEXT,
            author_login TEXT NOT NULL,
            committed_at DATETIME NOT NULL,
            message TEXT NOT NULL,
            stats_additions INTEGER NOT NULL,
            stats_deletions INTEGER NOT NULL,
            files_changed INTEGER NOT NULL,
            grade TEXT NOT NULL,
            rationale TEXT NOT NULL,
            model TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (repo_id, sha),
            FOREIGN KEY(repo_id) REFERENCES repos(id)
        )"
    ).execute(&pool).await?;

    sqlx::query(
        "CREATE TABLE IF NOT EXISTS user_memory (
            author_login TEXT PRIMARY KEY,
            memory_text TEXT NOT NULL,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )"
    ).execute(&pool).await?;

    Ok(pool)
}

pub async fn add_repo(pool: &SqlitePool, owner: &str, name: &str, branch: &str) -> Result<i64> {
    sqlx::query("INSERT OR IGNORE INTO repos (owner, name, default_branch) VALUES (?, ?, ?)")
        .bind(owner)
        .bind(name)
        .bind(branch)
        .execute(pool)
        .await?;
    
    let (id,): (i64,) = sqlx::query_as("SELECT id FROM repos WHERE owner = ? AND name = ?")
        .bind(owner)
        .bind(name)
        .fetch_one(pool)
        .await?;
        
    Ok(id)
}

pub async fn get_repos(pool: &SqlitePool) -> Result<Vec<Repo>> {
    let repos = sqlx::query_as::<_, Repo>("SELECT id FROM repos")
        .fetch_all(pool)
        .await?;
    Ok(repos)
}

pub async fn get_repo_state(pool: &SqlitePool, repo_id: i64) -> Result<Option<String>> {
    let row: Option<(Option<String>,)> = sqlx::query_as("SELECT last_seen_sha FROM repo_state WHERE repo_id = ?")
        .bind(repo_id)
        .fetch_optional(pool)
        .await?;
    Ok(row.and_then(|r| r.0))
}

pub async fn update_repo_state(pool: &SqlitePool, repo_id: i64, sha: &str) -> Result<()> {
    sqlx::query("INSERT OR REPLACE INTO repo_state (repo_id, last_seen_sha, last_run_at) VALUES (?, ?, ?)")
        .bind(repo_id)
        .bind(sha)
        .bind(Utc::now())
        .execute(pool)
        .await?;
    Ok(())
}

pub async fn save_review(pool: &SqlitePool, review: &CommitReview) -> Result<()> {
    sqlx::query(
        "INSERT OR IGNORE INTO commit_reviews (
            repo_id, sha, author_login, committed_at, message,
            stats_additions, stats_deletions, files_changed,
            grade, rationale, model, created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    )
    .bind(review.repo_id)
    .bind(&review.sha)
    .bind(&review.author_login)
    .bind(review.committed_at)
    .bind(&review.message)
    .bind(review.stats_additions)
    .bind(review.stats_deletions)
    .bind(review.files_changed)
    .bind(&review.grade)
    .bind(&review.rationale)
    .bind(&review.model)
    .bind(review.created_at)
    .execute(pool)
    .await?;
    Ok(())
}

pub async fn get_recent_reviews(pool: &SqlitePool, limit: i64) -> Result<Vec<CommitReview>> {
    let reviews = sqlx::query_as::<_, CommitReview>(
        r#"SELECT repo_id, sha, author_login, committed_at, message, stats_additions, stats_deletions, files_changed, grade, rationale, model, created_at FROM commit_reviews ORDER BY committed_at DESC LIMIT ?"#
    )
    .bind(limit)
    .fetch_all(pool)
    .await?;
    Ok(reviews)
}

pub async fn is_commit_graded(pool: &SqlitePool, repo_id: i64, sha: &str) -> Result<bool> {
    let count: (i64,) = sqlx::query_as("SELECT COUNT(*) FROM commit_reviews WHERE repo_id = ? AND sha = ?")
        .bind(repo_id)
        .bind(sha)
        .fetch_one(pool)
        .await?;
    Ok(count.0 > 0)
}

pub async fn get_memory(pool: &SqlitePool, author_login: &str) -> Result<Option<String>> {
    let row: Option<(String,)> = sqlx::query_as("SELECT memory_text FROM user_memory WHERE author_login = ?")
        .bind(author_login)
        .fetch_optional(pool)
        .await?;
    Ok(row.map(|r| r.0))
}

pub async fn update_memory(pool: &SqlitePool, author_login: &str, memory_text: &str) -> Result<()> {
    sqlx::query("INSERT OR REPLACE INTO user_memory (author_login, memory_text, updated_at) VALUES (?, ?, ?)")
        .bind(author_login)
        .bind(memory_text)
        .bind(Utc::now())
        .execute(pool)
        .await?;
    Ok(())
}

pub async fn get_all_memories(pool: &SqlitePool) -> Result<Vec<(String, String)>> {
    let rows: Vec<(String, String)> = sqlx::query_as("SELECT author_login, memory_text FROM user_memory")
        .fetch_all(pool)
        .await?;
    Ok(rows)
}

pub async fn ensure_user(pool: &SqlitePool, author_login: &str) -> Result<()> {
    sqlx::query("INSERT OR IGNORE INTO user_memory (author_login, memory_text) VALUES (?, ?)")
        .bind(author_login)
        .bind("New user, no history yet.")
        .execute(pool)
        .await?;
    Ok(())
}

pub async fn get_tracked_users(pool: &SqlitePool) -> Result<Vec<String>> {
    let rows: Vec<(String,)> = sqlx::query_as(
        "SELECT DISTINCT author_login FROM commit_reviews 
         UNION 
         SELECT DISTINCT author_login FROM user_memory"
    )
    .fetch_all(pool)
    .await?;
    Ok(rows.into_iter().map(|r| r.0).collect())
}
