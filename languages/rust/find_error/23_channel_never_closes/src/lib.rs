use std::sync::mpsc;
use std::thread;

/// Sums each chunk on its own worker thread and adds up the partial results.
pub fn parallel_sum(chunks: Vec<Vec<u64>>) -> u64 {
    let (tx, rx) = mpsc::channel();

    for chunk in chunks {
        let tx = tx.clone();
        thread::spawn(move || {
            let partial: u64 = chunk.iter().sum();
            tx.send(partial).unwrap();
        });
    }

    let mut total = 0;
    for partial in rx {
        total += partial;
    }
    total
}

/// Applies `f` to every item on worker threads; results come back in input order.
pub fn parallel_map(items: Vec<u32>, f: fn(u32) -> u32) -> Vec<u32> {
    let (tx, rx) = mpsc::channel();

    for (idx, item) in items.into_iter().enumerate() {
        let tx = tx.clone();
        thread::spawn(move || {
            tx.send((idx, f(item))).unwrap();
        });
    }

    let mut results: Vec<(usize, u32)> = rx.iter().collect();
    results.sort();
    results.into_iter().map(|(_, v)| v).collect()
}
