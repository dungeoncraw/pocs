use clap::Parser;

#[derive(Parser)]
#[command(name = "raquel")]
#[command(about = "A daily CLI agent that grades GitHub commits", long_about = None)]
pub struct Cli {
    #[arg(short, long, env = "DATABASE_URL", default_value = "sqlite:raquel.db")]
    pub db: String,

    #[arg(long, env = "GITHUB_TOKEN")]
    pub github_token: Option<String>,

    #[arg(long, env = "OPENAI_API_KEY")]
    pub openai_api_key: Option<String>,

    #[arg(long, env = "OPENAI_MODEL", default_value = "gpt-4o-mini")]
    pub openai_model: String,
}
