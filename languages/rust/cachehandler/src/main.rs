use std::collections::VecDeque;
use std::sync::Arc;
use tokio::sync::Mutex;

type MessageId = u64;

const CACHE_LIMIT: usize = 10;
const CANDIDATES: [MessageId; 12] = [
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
];

async fn mock_handler(cache: Arc<Mutex<VecDeque<MessageId>>>) {
    let selected = {
        let mut recent = cache.lock().await;

        let candidate = CANDIDATES
            .iter()
            .copied()
            .find(|id| !recent.contains(id));

        if let Some(id) = candidate {
            recent.push_back(id);

            while recent.len() > CACHE_LIMIT {
                recent.pop_front();
            }
        }

        candidate
    };

    match selected {
        Some(id) => println!("Selected message: {id}"),
        None => println!("No message available outside the recent cache"),
    }
}

#[tokio::main]
async fn main() {
    let cache = Arc::new(Mutex::new(VecDeque::<MessageId>::new()));

    let mut tasks = Vec::new();

    for _ in 0..15 {
        let shared_cache = Arc::clone(&cache);

        tasks.push(tokio::spawn(mock_handler(shared_cache)));
    }

    for task in tasks {
        task.await.expect("Handler task failed");
    }

    let recent = cache.lock().await;
    println!("Final cache: {recent:?}");
}