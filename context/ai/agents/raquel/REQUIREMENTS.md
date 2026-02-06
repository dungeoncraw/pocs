## Goal: a daily CLI agent that grades recent GitHub commits and persists results
You want a Rust CLI that, each day:
1. **Fetches new commits** for a GitHub user (usually across one or more repos).
2. **Evaluates commit quality** (message, size, tests/docs, cohesion, etc.).
3. **Assigns a grade** `A/B/C/D` (+ short rationale).
4. **Stores results and progress in a DB** so the next run only processes new commits.

That’s a good fit for:
- **GitHub API** for commit history + diffs
- **OpenAI** for rubric-based evaluation
- **SQLite** (simple local DB) for state + history
- **CLI** with `clap`

## Key product decisions (to clarify before coding)
### 1) Scope: “a GitHub user” across _which_ repos?
GitHub doesn’t provide a perfect “all commits by user across all repos” endpoint with diffs. The practical choices are:
- **A. Track a configured set of repos** (recommended)
    - You store a list: `owner/repo` pairs to track.
    - You can fetch commits + file changes reliably.

- **B. Track all repos the user owns**
    - Works for personal repos; misses org repos unless you also list org repos.

- **C. Track user “events”**
    - Can miss data and doesn’t always give diffs reliably.

I recommend **A**: user configures repos to track.
### 2) Grading is per-commit or per-day?
Do both:
- Store **per-commit** grade + rationale.
- Produce **daily summary** (average grade, trends).

### 3) API auth
- **GitHub token** (optional but strongly recommended to avoid rate limits).
- **OpenAI API key**.

## Architecture (modules)
- `github.rs`
    - Fetch commits since last seen SHA/date
    - Fetch commit details (files changed, additions/deletions, patch snippets if available)

- `grader.rs`
    - Build a compact prompt
    - Call OpenAI and parse a strict JSON response `{grade, reasons, flags}`

- `db.rs`
    - SQLite schema + queries
    - Save state: last processed commit per repo

- /
    - Commands: `init`, `add-repo`, `run`, `report`

`cli.rs``main.rs`

## Database schema (SQLite)
Minimum tables:
- `repos`
    - `id`, , `name`, `default_branch` `owner`

- `repo_state`
    - `repo_id`, `last_seen_sha`, `last_run_at`

- `commit_reviews`
    - `repo_id`, `sha`, `author_login`, `committed_at`
    - `message`, `stats_additions`, `stats_deletions`, `files_changed`
    - `grade` (`A/B/C/D`)
    - `rationale` (text)
    - `model`, `prompt_hash`, `created_at`
    - unique constraint `(repo_id, sha)` so reruns are idempotent

Optional:
- `daily_summaries` for cached reports
- `notes` / “memory” table if you want longitudinal coaching (“common issues: missing tests”)

## GitHub data to fetch (practical endpoints)
For each tracked repo:
1. **List commits** (filter by author if you want only that user’s commits):

- `GET /repos/{owner}/{repo}/commits?author={login}&sha={branch}&per_page=...`

1. For each new SHA, get details (includes file list and stats):

- `GET /repos/{owner}/{repo}/commits/{sha}`
    - Includes `files[]` with `filename`, `additions`, `deletions`, and often `patch` (may be truncated)

This is usually enough to grade “commit quality” without cloning the repo.
## Grading rubric (what “quality” means)
You’ll get much better consistency if you **combine heuristics + LLM**:
### Heuristics (fast, deterministic)
- Commit message length, conventional commit style
- “WIP”, “tmp”, “fix stuff” → penalize
- Very large changes (e.g., 2000+ lines) → penalize unless message explains (vendor/import)
- Many unrelated files → penalize
- Presence of tests/docs changes when touching core code → bonus

### LLM-based evaluation (contextual)
Send:
- commit message
- stats (files changed, additions/deletions)
- file paths
- short patch excerpts (limited) Ask for:
- grade `A/B/C/D`
- 3–6 bullet reasons
- suggested improvement

**Important:** require **strict JSON output**, so you can parse it safely.
Example required output:``` json
{"grade":"B","reasons":["..."],"flags":["missing_tests"],"summary":"..."}
```

 
CLI shape
Suggested commands:
agent init --db ./agent.db --user raquel
Creates tables, saves config.
agent add-repo owner/name [--branch main]
Adds repo to track.
agent run [--since 2026-01-01] [--max 50]
Fetches new commits since repo_state.last_seen_sha, grades, saves.
agent report [--days 30]
Shows grades trend + recent commit grades.
Environment variables:
OPENAI_API_KEY
GITHUB_TOKEN (optional)
maybe OPENAI_MODEL default gpt-4o-mini/whatever you choose
 
Rust crate choices (concrete)
CLI: clap
Async runtime: tokio
HTTP: reqwest
JSON: serde, serde_json
DB (SQLite): sqlx with sqlite feature
Time: time or chrono
Logging: tracing
OpenAI:
async-openai is commonly used in Rust for OpenAI’s API.
 
Processing loop (idempotent “daily run”)
Pseudo-flow:
Load tracked repos.
For each repo:
read last_seen_sha
list commits by author since last seen (or fetch latest N and stop when you hit known SHA)
For each unseen commit (oldest → newest):
fetch commit details
build grading input (with truncation limits)
call OpenAI → parse JSON grade
insert into commit_reviews
update repo_state.last_seen_sha to this SHA
This makes reruns safe: if the program crashes mid-way, the next run continues.
 
What I need from you to produce exact Rust code edits in your project
Because I can’t reliably read your current Cargo.toml / src/main.rs from here, paste these two files and answer:
Do you want SQLite (local file) or Postgres?
Do you want to track:
one repo, or
multiple repos, configured via add-repo?
The GitHub “user” is identified by:
login (e.g. octocat) and we filter commits by author=login — OK?
Once you paste Cargo.toml and src/main.rs, I’ll provide:
the exact dependency additions,
the DB schema + sqlx code,
GitHub client code,
OpenAI grading call with strict JSON parsing,
and the full CLI with init/add-repo/run/report.