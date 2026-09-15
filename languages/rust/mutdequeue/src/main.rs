use std::collections::VecDeque;
use std::sync::{Arc, Mutex};
use std::thread;

type MessageId = u64;

const MAX_RECENT_MESSAGES: usize = 10;

#[derive(Clone)]
struct RecentMessages {
    ids: Arc<Mutex<VecDeque<MessageId>>>,
}

impl RecentMessages {
    fn new() -> Self {
        Self {
            ids: Arc::new(Mutex::new(VecDeque::with_capacity(
                MAX_RECENT_MESSAGES,
            ))),
        }
    }

    fn record(&self, id: MessageId) {
        let mut ids = self.ids.lock().expect("Recent messages lock poisoned");

        if ids.len() == MAX_RECENT_MESSAGES {
            ids.pop_front();
        }

        ids.push_back(id);
    }

    fn contains(&self, id: MessageId) -> bool {
        let ids = self.ids.lock().expect("Recent messages lock poisoned");

        ids.contains(&id)
    }

    fn exclude_recent(&self, candidates: &[MessageId]) -> Vec<MessageId> {
        let ids = self.ids.lock().expect("Recent messages lock poisoned");

        candidates
            .iter()
            .copied()
            .filter(|id| !ids.contains(id))
            .collect()
    }

    fn snapshot(&self) -> Vec<MessageId> {
        let ids = self.ids.lock().expect("Recent messages lock poisoned");

        ids.iter().copied().collect()
    }
}

fn main() {
    let recent = RecentMessages::new();

    let worker_cache = recent.clone();

    let worker = thread::spawn(move || {
        for id in 1..=12 {
            worker_cache.record(id);
        }
    });

    worker.join().expect("Worker thread panicked");

    assert_eq!(recent.snapshot(), (3..=12).collect::<Vec<_>>());
    assert!(!recent.contains(2));
    assert!(recent.contains(12));

    let candidates = vec![1, 2, 3, 10, 12, 13];
    let eligible = recent.exclude_recent(&candidates);

    assert_eq!(eligible, vec![1, 2, 13]);

    println!("Recent messages: {:?}", recent.snapshot());
    println!("Eligible messages: {:?}", eligible);
}