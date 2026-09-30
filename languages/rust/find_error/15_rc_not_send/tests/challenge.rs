use challenge_15_rc_not_send::*;
use std::thread;

fn assert_send_sync<T: Send + Sync>() {}

#[test]
fn counter_is_send_and_sync() {
    assert_send_sync::<SharedCounter>();
}

#[test]
fn parallel_helper_counts_every_increment() {
    assert_eq!(count_in_parallel(8, 1_000), 8_000);
}

#[test]
fn caller_can_share_counter_across_threads() {
    let counter = SharedCounter::new();
    let handles: Vec<_> = (1..=4u64)
        .map(|i| {
            let c = counter.clone();
            thread::spawn(move || {
                for _ in 0..500 {
                    c.increment();
                }
                c.add(i);
            })
        })
        .collect();
    for h in handles {
        h.join().unwrap();
    }
    assert_eq!(counter.get(), 4 * 500 + 10);
}
