use challenge_23_channel_never_closes::*;
use std::sync::mpsc;
use std::thread;
use std::time::Duration;

/// Runs `f` on a helper thread and fails (instead of hanging) after 3 seconds.
fn with_timeout<T: Send + 'static>(f: impl FnOnce() -> T + Send + 'static) -> T {
    let (tx, rx) = mpsc::channel();
    thread::spawn(move || {
        let _ = tx.send(f());
    });
    rx.recv_timeout(Duration::from_secs(3))
        .expect("timed out: the function never returned")
}

#[test]
fn sums_all_chunks() {
    let chunks = vec![vec![1, 2, 3], vec![10, 20], vec![100]];
    assert_eq!(with_timeout(move || parallel_sum(chunks)), 136);
}

#[test]
fn sums_many_chunks() {
    let chunks: Vec<Vec<u64>> = (0..50).map(|i| vec![i; 10]).collect();
    let expected: u64 = (0..50u64).map(|i| i * 10).sum();
    assert_eq!(with_timeout(move || parallel_sum(chunks)), expected);
}

#[test]
fn empty_input_sums_to_zero() {
    assert_eq!(with_timeout(|| parallel_sum(vec![])), 0);
}

#[test]
fn map_preserves_order() {
    let out = with_timeout(|| parallel_map(vec![1, 2, 3, 4, 5], |x| x * x));
    assert_eq!(out, vec![1, 4, 9, 16, 25]);
}
