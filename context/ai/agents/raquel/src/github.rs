use reqwest::header::{HeaderMap, HeaderValue, AUTHORIZATION, USER_AGENT};
use serde::Deserialize;
use anyhow::Result;
use chrono::{DateTime, Utc};

#[derive(Debug, Deserialize)]
pub struct GithubCommit {
    pub sha: String,
    pub commit: CommitDetails,
    pub author: Option<Author>,
}

#[derive(Debug, Deserialize)]
pub struct CommitDetails {
    pub message: String,
    pub author: CommitAuthor,
}

#[derive(Debug, Deserialize)]
pub struct CommitAuthor {
    pub date: DateTime<Utc>,
}

#[derive(Debug, Deserialize)]
pub struct Author {
    pub login: String,
}

#[derive(Debug, Deserialize)]
pub struct FullCommit {
    pub sha: String,
    pub commit: CommitDetails,
    pub stats: Stats,
    pub files: Vec<File>,
}

#[derive(Debug, Deserialize)]
pub struct Stats {
    pub additions: i32,
    pub deletions: i32,
    pub total: i32,
}

#[derive(Debug, Deserialize)]
pub struct File {
    pub filename: String,
    pub additions: i32,
    pub deletions: i32,
    pub patch: Option<String>,
}

#[derive(Debug, Deserialize)]
pub struct GithubRepo {
    pub name: String,
    pub owner: Author,
    pub default_branch: String,
}

pub struct GithubClient {
    client: reqwest::Client,
}

impl GithubClient {
    pub fn new(token: Option<String>) -> Result<Self> {
        let mut headers = HeaderMap::new();
        headers.insert(USER_AGENT, HeaderValue::from_static("raquel-agent"));
        if let Some(t) = token {
            headers.insert(AUTHORIZATION, HeaderValue::from_str(&format!("Bearer {}", t))?);
        }

        let client = reqwest::Client::builder()
            .default_headers(headers)
            .build()?;
        Ok(Self { client })
    }

    pub async fn list_user_repos(&self, user: &str) -> Result<Vec<GithubRepo>> {
        let url = format!("https://api.github.com/users/{}/repos?type=all&sort=updated", user);
        let repos = self.client.get(&url)
            .send()
            .await?
            .json::<Vec<GithubRepo>>()
            .await?;
        Ok(repos)
    }

    pub async fn list_commits(&self, owner: &str, repo: &str, author: &str, since: Option<DateTime<Utc>>) -> Result<Vec<GithubCommit>> {
        let mut url = format!("https://api.github.com/repos/{}/{}/commits?author={}", owner, repo, author);
        if let Some(s) = since {
            url.push_str(&format!("&since={}", s.to_rfc3339()));
        }

        let commits = self.client.get(&url)
            .send()
            .await?
            .json::<Vec<GithubCommit>>()
            .await?;
        Ok(commits)
    }

    pub async fn get_commit_details(&self, owner: &str, repo: &str, sha: &str) -> Result<FullCommit> {
        let url = format!("https://api.github.com/repos/{}/{}/commits/{}", owner, repo, sha);
        let commit = self.client.get(&url)
            .send()
            .await?
            .json::<FullCommit>()
            .await?;
        Ok(commit)
    }
}
