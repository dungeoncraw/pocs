use challenge_30_quadratic_perf::*;
use std::collections::HashSet;
use std::sync::mpsc;
use std::thread;
use std::time::{Duration, Instant};

const N: u64 = 200_000;

fn run_with_limit<T: Send + 'static>(secs: u64, f: impl FnOnce() -> T + Send + 'static) -> T {
    let (tx, rx) = mpsc::channel();
    thread::spawn(move || {
        let start = Instant::now();
        let out = f();
        let _ = tx.send((out, start.elapsed()));
    });
    match rx.recv_timeout(Duration::from_secs(secs)) {
        Ok((out, _)) => out,
        Err(_) => panic!("took longer than {secs}s: algorithm does not scale"),
    }
}

fn events() -> Vec<u64> {
    (0..N).map(|i| (i * 7919) % 190_001).collect()
}

#[test]
fn dedup_small_input_keeps_first_occurrences() {
    assert_eq!(dedup_stable(&[3, 1, 3, 2, 1, 4]), [3, 1, 2, 4]);
}

#[test]
fn batches_small_input() {
    assert_eq!(drain_batches(vec![1, 2, 3, 4, 5], 2), vec![vec![1, 2], vec![3, 4], vec![5]]);
}

#[test]
fn dedup_scales_to_200k_events() {
    let input = events();
    let expected: Vec<u64> = {
        let mut seen = HashSet::new();
        input.iter().copied().filter(|e| seen.insert(*e)).collect()
    };
    let got = run_with_limit(3, move || dedup_stable(&input));
    assert_eq!(got, expected);
}

#[test]
fn batching_scales_to_200k_events() {
    let queue: Vec<u64> = (0..N).collect();
    let batches = run_with_limit(3, move || drain_batches(queue, 100));
    assert_eq!(batches.len(), 2000);
    assert!(batches.iter().all(|b| b.len() == 100));
    assert_eq!(batches[1999][99], N - 1);
    assert_eq!(batches[7][0], 700);
}

#[test]
fn full_pipeline_scales() {
    let input = events();
    let unique = input.iter().collect::<HashSet<_>>().len();
    let out = run_with_limit(4, move || ingest(&input, 500));
    assert_eq!(out.iter().map(Vec::len).sum::<usize>(), unique);
    assert_eq!(out[0][..3], [0, 7919, 15838]);
}
