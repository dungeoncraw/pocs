use rayon::prelude::*;
use std::hint::black_box;
use std::time::{Duration, Instant};

const MESSAGE_COUNT: usize = 100_000;
const RUNS: u32 = 5;

#[derive(Debug)]
struct Message {
    id: u64,
    weight: u64,
    times_served: u64,
    text: String,
}

fn score_message(message: &Message) -> u64 {
    let mut score =
        message.id
            .wrapping_mul(31)
            .wrapping_add(message.weight)
            .wrapping_sub(message.times_served);

    for round in 0..500 {
        score = score
            .wrapping_mul(31)
            .wrapping_add(round)
            .wrapping_add(message.text.len() as u64);

        score ^= score.rotate_left((round % 63 + 1) as u32);
    }

    score
}

fn sequential(messages: &[Message]) -> Vec<u64> {
    messages
        .iter()
        .map(score_message)
        .collect()
}

fn parallel(messages: &[Message]) -> Vec<u64> {
    messages
        .par_iter()
        .map(score_message)
        .collect()
}

fn main() {
    let messages: Vec<Message> = (0..MESSAGE_COUNT)
        .map(|id| Message {
            id: id as u64,
            weight: (id % 10 + 1) as u64,
            times_served: (id % 20) as u64,
            text: format!("Candidate message {id}"),
        })
        .collect();

    black_box(
        messages[..1_000]
            .par_iter()
            .map(score_message)
            .reduce(|| 0, u64::wrapping_add),
    );

    let mut sequential_total = Duration::ZERO;
    let mut parallel_total = Duration::ZERO;

    for _ in 0..RUNS {
        let start = Instant::now();

        let sequential_scores = sequential(&messages);

        sequential_total += start.elapsed();

        let start = Instant::now();

        let parallel_scores = parallel(&messages);

        parallel_total += start.elapsed();

        assert_eq!(sequential_scores, parallel_scores);

        black_box(sequential_scores);
        black_box(parallel_scores);
    }

    let sequential_average = sequential_total / RUNS;
    let parallel_average = parallel_total / RUNS;

    println!("Messages: {}", messages.len());
    println!("Rayon threads: {}", rayon::current_num_threads());

    println!(
        "Sequential: {:.2?}",
        sequential_average
    );

    println!(
        "Parallel:   {:.2?}",
        parallel_average
    );

    println!(
        "Speedup: {:.2}x",
        sequential_average.as_secs_f64()
            / parallel_average.as_secs_f64()
    );
}