/// Remove duplicate event ids, keeping the first occurrence of each.
pub fn dedup_stable(events: &[u64]) -> Vec<u64> {
    let mut seen: Vec<u64> = Vec::new();
    for &e in events {
        if !seen.contains(&e) {
            seen.push(e);
        }
    }
    seen
}

/// Split a queue of events into batches of at most `size`, preserving order.
pub fn drain_batches(mut queue: Vec<u64>, size: usize) -> Vec<Vec<u64>> {
    assert!(size > 0);
    let mut batches = Vec::new();
    while !queue.is_empty() {
        let mut batch = Vec::with_capacity(size);
        while batch.len() < size && !queue.is_empty() {
            batch.push(queue.remove(0));
        }
        batches.push(batch);
    }
    batches
}

/// Dedup then batch: the ingestion pipeline entry point.
pub fn ingest(events: &[u64], batch_size: usize) -> Vec<Vec<u64>> {
    drain_batches(dedup_stable(events), batch_size)
}
