mod agent;

use agent::{StockReplicatorAgent, Tweet};
use std::env;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    dotenv::dotenv().ok();
    
    if env::var("OPENAI_API_KEY").is_err() {
        eprintln!("Error: OPENAI_API_KEY environment variable is not set.");
        eprintln!("Please provide a valid API key to run the stock replicator agent.");
        std::process::exit(1);
    }

    let agent = StockReplicatorAgent::new();

    // Sample Twitter/X data
    let tweets = vec![
        Tweet {
            author: "TechGuru".to_string(),
            content: "NVDA earnings just blew expectations out of the water! AI demand is insane. 🚀 #stocks #NVDA".to_string(),
            timestamp: "2026-06-24 10:00".to_string(),
        },
        Tweet {
            author: "MarketWatch".to_string(),
            content: "Tesla is facing supply chain issues in Europe, delivery estimates pushed back. $TSLA".to_string(),
            timestamp: "2026-06-24 10:05".to_string(),
        },
        Tweet {
            author: "TraderJoe".to_string(),
            content: "Apple's new headset is getting mixed reviews, might wait for the next version. $AAPL".to_string(),
            timestamp: "2026-06-24 10:10".to_string(),
        },
    ];

    println!("--- Stock Replicator Agent ---");
    println!("Analyzing {} tweets for market tendencies...\n", tweets.len());

    match agent.analyze_tweets(&tweets).await {
        Ok(signals) => {
            println!("Analysis Results:");
            for signal in signals {
                println!("-----------------------------------");
                println!("Ticker:     {}", signal.ticker);
                println!("Action:     {:?}", signal.action);
                println!("Confidence: {:.2}", signal.confidence);
                println!("Reasoning:  {}", signal.reasoning);
            }
            println!("-----------------------------------");
        }
        Err(e) => {
            eprintln!("Error during analysis: {}", e);
        }
    }

    Ok(())
}
