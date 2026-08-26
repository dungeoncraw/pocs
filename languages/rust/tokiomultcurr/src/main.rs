use std::time::Instant;
use tokio::runtime::Builder;

const TASKS: usize = 100;

fn cpu_work() -> u64 {
    let mut value = 0u64;

    for i in 0..50_000_000 {
        value = value.wrapping_add(i);
    }

    value
}

async fn workload() {
    let mut handles = Vec::with_capacity(TASKS);

    for _ in 0..TASKS {
        handles.push(tokio::spawn(async {
            cpu_work()
        }));
    }

    for handle in handles {
        handle.await.unwrap();
    }
}

fn main() {
    let multi_thread = Builder::new_multi_thread()
        .enable_all()
        .build()
        .unwrap();

    let start = Instant::now();

    multi_thread.block_on(workload());

    println!("multi_thread: {:?}", start.elapsed());

    drop(multi_thread);

    let current_thread = Builder::new_current_thread()
        .enable_all()
        .build()
        .unwrap();

    let start = Instant::now();

    current_thread.block_on(workload());

    println!("current_thread: {:?}", start.elapsed());
}