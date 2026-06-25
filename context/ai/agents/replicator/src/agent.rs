use serde::{Deserialize, Serialize};
use reqwest::Client;
use std::env;

#[derive(Debug, Serialize, Deserialize)]
pub enum Action {
    Buy,
    Sell,
    Hold,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Signal {
    pub ticker: String,
    pub action: Action,
    pub reasoning: String,
    pub confidence: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Tweet {
    pub author: String,
    pub content: String,
    pub timestamp: String,
}

#[derive(Debug, Serialize)]
struct OpenAIRequest {
    model: String,
    messages: Vec<Message>,
}

#[derive(Debug, Serialize)]
struct Message {
    role: String,
    content: String,
}

#[derive(Debug, Deserialize)]
struct OpenAIResponse {
    choices: Vec<Choice>,
}

#[derive(Debug, Deserialize)]
struct Choice {
    message: ResponseMessage,
}

#[derive(Debug, Deserialize)]
struct ResponseMessage {
    content: String,
}

pub struct StockReplicatorAgent {
    client: Client,
    api_key: String,
}

impl StockReplicatorAgent {
    pub fn new() -> Self {
        dotenv::dotenv().ok();
        let api_key = env::var("OPENAI_API_KEY").expect("OPENAI_API_KEY must be set");
        Self {
            client: Client::new(),
            api_key,
        }
    }

    pub async fn analyze_tweets(&self, tweets: &[Tweet]) -> Result<Vec<Signal>, Box<dyn std::error::Error>> {
        let tweets_json = serde_json::to_string(tweets)?;
        
        let prompt = format!(
            "Analyze the following Twitter/X messages and identify stock market tendencies. 
            For each relevant stock mentioned or implied, suggest an action: Buy, Sell, or Hold.
            Return the result as a JSON array of objects with this structure:
            [
                {{
                    \"ticker\": \"TICKER\",
                    \"action\": \"Buy\" | \"Sell\" | \"Hold\",
                    \"reasoning\": \"brief explanation\",
                    \"confidence\": 0.0 to 1.0
                }}
            ]

            Tweets:
            {}",
            tweets_json
        );

        let request = OpenAIRequest {
            model: "gpt-4o".to_string(),
            messages: vec![
                Message {
                    role: "system".to_string(),
                    content: "You are a financial analyst specializing in social media sentiment analysis.".to_string(),
                },
                Message {
                    role: "user".to_string(),
                    content: prompt,
                },
            ],
        };

        let response = self.client
            .post("https://api.openai.com/v1/chat/completions")
            .header("Authorization", format!("Bearer {}", self.api_key))
            .json(&request)
            .send()
            .await?;

        let open_ai_response: OpenAIResponse = response.json().await?;
        let content = open_ai_response.choices[0].message.content.clone();
        
        let clean_content = content.trim_start_matches("```json").trim_end_matches("```").trim();
        
        let signals: Vec<Signal> = serde_json::from_str(clean_content)?;
        Ok(signals)
    }
}
