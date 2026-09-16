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

    fn force_poison(&self) {
        let _guard = self.ids.lock().expect("Recent messages lock poisoned");
        panic!("Simulated worker panic while holding the lock to induce mutex poisoning");
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

    println!("\n--- Forcing Mutex Poisoning Scenario ---");
    let poison_target = recent.clone();
    let poison_thread = thread::spawn(move || {
        poison_target.force_poison();
    });

    // The thread panics while holding the lock
    let join_result = poison_thread.join();
    assert!(join_result.is_err(), "Expected thread to panic");
    println!("Worker thread panicked while holding the mutex guard.");

    // Subsequent lock attempts will now observe a PoisonError
    match recent.ids.lock() {
        Ok(_) => panic!("Expected mutex to be poisoned!"),
        Err(poison_err) => {
            println!("Verified Mutex is poisoned: {}", poison_err);
            let recovered_data = poison_err.into_inner();
            println!("Recovered state from poisoned lock: {:?}", *recovered_data);
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_mutex_poisoning_scenario() {
        let recent = RecentMessages::new();
        recent.record(42);

        let poison_target = recent.clone();
        let handle = thread::spawn(move || {
            poison_target.force_poison();
        });

        // Ensure the worker thread actually panicked
        let res = handle.join();
        assert!(res.is_err(), "Thread must panic while holding the lock");

        // Verify the mutex is now poisoned on subsequent access
        let lock_res = recent.ids.lock();
        assert!(lock_res.is_err(), "Mutex should return a PoisonError");

        // Verify that data can still be inspected or salvaged via into_inner()
        let recovered = lock_res.unwrap_err().into_inner();
        assert_eq!(recovered.len(), 1);
        assert_eq!(recovered[0], 42);
    }
}