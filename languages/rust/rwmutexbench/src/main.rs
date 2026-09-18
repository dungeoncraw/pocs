use std::{
    collections::VecDeque,
    hint::black_box,
    sync::Arc,
    time::{Duration, Instant},
};
use tokio::{
    sync::{Barrier, Mutex, RwLock},
    task::JoinSet,
};

type MessageId = u64;

const CAPACITY: usize = 10;
const TASKS: usize = 16;
const OPS_PER_TASK: usize = 10_000;
const WRITE_EVERY: usize = 100;
const ROUNDS: usize = 5;

#[derive(Clone, Copy)]
enum LockKind {
    Mutex,
    RwLock,
}

enum Cache {
    Mutex(Mutex<VecDeque<MessageId>>),
    RwLock(RwLock<VecDeque<MessageId>>),
}

fn remember(queue: &mut VecDeque<MessageId>, id: MessageId) {
    if queue.len() == CAPACITY {
        queue.pop_front();
    }

    queue.push_back(id);
}

impl Cache {
    fn new(kind: LockKind) -> Self {
        let queue = (0..CAPACITY as MessageId).collect();

        match kind {
            LockKind::Mutex => Self::Mutex(Mutex::new(queue)),
            LockKind::RwLock => Self::RwLock(RwLock::new(queue)),
        }
    }

    async fn contains(&self, id: MessageId) -> bool {
        match self {
            Self::Mutex(lock) => {
                let queue = lock.lock().await;
                queue.contains(&id)
            }
            Self::RwLock(lock) => {
                let queue = lock.read().await;
                queue.contains(&id)
            }
        }
    }

    async fn remember(&self, id: MessageId) {
        match self {
            Self::Mutex(lock) => {
                let mut queue = lock.lock().await;
                remember(&mut queue, id);
            }
            Self::RwLock(lock) => {
                let mut queue = lock.write().await;
                remember(&mut queue, id);
            }
        }
    }
}

async fn benchmark(kind: LockKind) -> Duration {
    let cache = Arc::new(Cache::new(kind));
    let barrier = Arc::new(Barrier::new(TASKS + 1));
    let mut tasks = JoinSet::new();

    for task_id in 0..TASKS {
        let cache = Arc::clone(&cache);
        let barrier = Arc::clone(&barrier);

        tasks.spawn(async move {
            barrier.wait().await;

            let mut hits = 0_u64;

            for operation in 0..OPS_PER_TASK {
                let id = ((operation * 17 + task_id * 31) % 64) as MessageId;

                if (operation + task_id) % WRITE_EVERY == 0 {
                    cache.remember(id).await;
                } else {
                    hits += u64::from(cache.contains(id).await);
                }
            }

            hits
        });
    }

    let start = Instant::now();
    barrier.wait().await;

    let mut total_hits = 0_u64;

    while let Some(result) = tasks.join_next().await {
        total_hits += result.expect("benchmark task failed");
    }

    let elapsed = start.elapsed();
    black_box(total_hits);
    elapsed
}

fn median(samples: &mut [Duration]) -> Duration {
    samples.sort_unstable();
    samples[samples.len() / 2]
}

fn report(name: &str, elapsed: Duration) {
    let operations = (TASKS * OPS_PER_TASK) as f64;
    let throughput = operations / elapsed.as_secs_f64();

    println!(
        "{name:6}: {:8.2} ms | {:12.0} operations/s",
        elapsed.as_secs_f64() * 1_000.0,
        throughput
    );
}

#[tokio::main(flavor = "multi_thread", worker_threads = 4)]
async fn main() {
    benchmark(LockKind::Mutex).await;
    benchmark(LockKind::RwLock).await;

    let mut mutex_samples = Vec::with_capacity(ROUNDS);
    let mut rwlock_samples = Vec::with_capacity(ROUNDS);

    for round in 0..ROUNDS {
        if round % 2 == 0 {
            mutex_samples.push(benchmark(LockKind::Mutex).await);
            rwlock_samples.push(benchmark(LockKind::RwLock).await);
        } else {
            rwlock_samples.push(benchmark(LockKind::RwLock).await);
            mutex_samples.push(benchmark(LockKind::Mutex).await);
        }
    }

    let mutex_time = median(&mut mutex_samples);
    let rwlock_time = median(&mut rwlock_samples);

    report("Mutex", mutex_time);
    report("RwLock", rwlock_time);

    println!(
        "Mutex time / RwLock time: {:.2}x",
        mutex_time.as_secs_f64() / rwlock_time.as_secs_f64()
    );
}