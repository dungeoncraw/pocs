# Raquel Agent 🚀

Raquel is a daily CLI agent written in Rust that automatically grades GitHub commits using AI and persists the results to provide longitudinal coaching and progress tracking.

## Key Features
- **Dynamic Interactive Mode**: Select users and grading periods via an intuitive terminal menu.
- **Automated AI Grading**: Evaluates commit quality (message, size, cohesion, tests/docs) using OpenAI.
- **Longitudinal Coaching**: Maintains a persistent "memory" of each user's patterns and progress.
- **Idempotent Processing**: Tracks the last processed commit to avoid duplicate grading.
- **Detailed Reporting**: Summarizes grades, rationales, and historical trends.

## Technical Stack
- **Language**: Rust
- **Async Runtime**: `tokio`
- **HTTP Client**: `reqwest`
- **Database**: SQLite (via `sqlx`)
- **AI Integration**: `async-openai`
- **CLI Framework**: `clap`

## Installation

Ensure you have [Rust](https://www.rust-lang.org/tools/install) installed.

```bash
git clone <repository-url>
cd raquel
cargo build --release
```

## Configuration

The agent requires the following environment variables. You can also use a `.env` file in the project root.

- `GITHUB_TOKEN`: A GitHub Personal Access Token (PAT) to fetch commits and diffs.
- `OPENAI_API_KEY`: Your OpenAI API key for grading.
- `OPENAI_MODEL`: (Optional) The model to use (default: `gpt-4o-mini`).
- `DATABASE_URL`: (Optional) SQLite connection string (default: `sqlite:raquel.db`).

## Usage

Raquel operates in **Dynamic Interactive Mode**. Simply run it without arguments to be guided through user selection and grading:

```bash
cargo run
```

### Interactive Features ✨
- **Main Menu**: Choose between grading a user or exiting.
- **Persistent Session**: Keep using the agent until you choose to exit.
- **Automatic Repository Discovery**: Raquel automatically finds all public repositories for the selected user.
- **Automatic User Detection**: Choose from a list of previously tracked GitHub users.
- **Onboarding**: If tokens are missing, Raquel will prompt you to enter them and optionally save them to a `.env` file for future use.
- **Dynamic Onboarding**: Easily add new users to the system.
- **Flexible Grading Windows**: Choose between "Today", the last 50 commits (idempotent), or a custom start date.
- **Instant Feedback**: Option to view the grading report immediately after processing.
- **Idempotency**: Raquel tracks processed commits and will never grade the same commit twice, even if requested.

## Project Structure
- `src/main.rs`: Orchestrates the CLI commands and execution flow.
- `src/cli.rs`: Defines the command-line interface and arguments.
- `src/db.rs`: Handles SQLite schema and data persistence.
- `src/github.rs`: Client for interacting with the GitHub API.
- `src/grader.rs`: Integration with OpenAI for grading and memory management.

## License
[MIT](LICENSE) (or specify your license)
