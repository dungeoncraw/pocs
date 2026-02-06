use async_openai::{
    types::{ChatCompletionRequestSystemMessageArgs, ChatCompletionRequestUserMessageArgs, CreateChatCompletionRequestArgs},
    Client,
};
use serde::{Deserialize, Serialize};
use anyhow::{Result, anyhow};
use crate::github::FullCommit;

#[derive(Debug, Serialize, Deserialize)]
pub struct GradingResponse {
    pub grade: String,
    pub reasons: Vec<String>,
    pub flags: Vec<String>,
    pub summary: String,
    pub updated_memory: Option<String>,
}

pub struct Grader {
    client: Client<async_openai::config::OpenAIConfig>,
    model: String,
}

impl Grader {
    pub fn new(api_key: String, model: Option<String>) -> Self {
        let config = async_openai::config::OpenAIConfig::new().with_api_key(api_key);
        let client = Client::with_config(config);
        let model = model.unwrap_or_else(|| "gpt-4o-mini".to_string());
        Self { client, model }
    }

    pub async fn grade_commit(&self, commit: &FullCommit, previous_memory: Option<&str>) -> Result<GradingResponse> {
        let system_prompt = "You are an expert code reviewer. Grade the following commit based on message quality, size, cohesion, and presence of tests/docs.
You also maintain a 'memory' of the user's past behavior to provide longitudinal coaching.
Return a strict JSON object with:
- grade: A, B, C, or D
- reasons: a list of 3-6 bullet points
- flags: a list of short strings (e.g. 'missing_tests', 'large_diff')
- summary: a short rationale sentence.
- updated_memory: An updated string representing your 'memory' of this user (lessons learned, recurring issues, or praise). Keep it concise (max 2-3 sentences).";

        let memory_context = previous_memory.map(|m| format!("\nPrevious memory of this user: {}\n", m)).unwrap_or_default();

        let mut commit_info = format!(
            "{}Message: {}\nStats: +{} -{} ({} files)\nFiles:\n",
            memory_context,
            commit.commit.message,
            commit.stats.additions,
            commit.stats.deletions,
            commit.files.len()
        );

        for file in commit.files.iter().take(10) {
            commit_info.push_str(&format!("- {} (+{} -{})\n", file.filename, file.additions, file.deletions));
            if let Some(patch) = &file.patch {
                let truncated_patch = patch.chars().take(500).collect::<String>();
                commit_info.push_str(&format!("  Patch: {}...\n", truncated_patch));
            }
        }

        let request = CreateChatCompletionRequestArgs::default()
            .model(&self.model)
            .messages([
                ChatCompletionRequestSystemMessageArgs::default()
                    .content(system_prompt)
                    .build()?
                    .into(),
                ChatCompletionRequestUserMessageArgs::default()
                    .content(commit_info)
                    .build()?
                    .into(),
            ])
            .response_format(async_openai::types::ResponseFormat::JsonObject)
            .build()?;

        let response = self.client.chat().create(request).await?;
        let choice = response.choices.first().ok_or_else(|| anyhow!("No response from OpenAI"))?;
        let content = choice.message.content.as_ref().ok_or_else(|| anyhow!("Empty response from OpenAI"))?;

        let grading: GradingResponse = serde_json::from_str(content)?;
        Ok(grading)
    }
}
