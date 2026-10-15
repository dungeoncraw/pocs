use std::sync::{Arc, Mutex};
use std::thread;

/// A counter handle that can be cloned and shared between workers.
#[derive(Clone)]
pub struct SharedCounter {
    inner: Arc<Mutex<u64>>,
}

impl SharedCounter {
    pub fn new() -> Self {
        SharedCounter {
            inner: Arc::new(Mutex::new(0)),
        }
    }

    pub fn increment(&self) {
        *self.inner.lock().unwrap() += 1;
    }

    pub fn add(&self, n: u64) {
        *self.inner.lock().unwrap() += n;
    }

    pub fn get(&self) -> u64 {
        *self.inner.lock().unwrap()
    }
}

impl Default for SharedCounter {
    fn default() -> Self {
        Self::new()
    }
}

/// Spawns `workers` threads that each increment a shared counter
/// `per_worker` times, and returns the final total.
pub fn count_in_parallel(workers: usize, per_worker: u64) -> u64 {
    let counter = SharedCounter::new();
    let handles: Vec<_> = (0..workers)
        .map(|_| {
            let c = counter.clone();
            thread::spawn(move || {
                for _ in 0..per_worker {
                    c.increment();
                }
            })
        })
        .collect();
    for h in handles {
        h.join().unwrap();
    }
    counter.get()
}
