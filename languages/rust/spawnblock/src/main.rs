use std::thread;
use std::time::{Duration, Instant};
use tokio::time::sleep;

fn heavy_sync_work() -> u64 {
    thread::sleep(Duration::from_secs(3));
    42
}

async fn heartbeat(label: &'static str) {
    for i in 1..=10 {
        println!("{label}: heartbeat {i}");
        sleep(Duration::from_millis(300)).await;
    }
}

async fn example_spawn() {
    println!("\n tokio::spawn");

    let start = Instant::now();

    let heartbeat = tokio::spawn(heartbeat("spawn"));

    tokio::task::yield_now().await;

    let work = tokio::spawn(async {
        println!("spawn: heavy work started");

        let result = heavy_sync_work();

        println!("spawn: heavy work finished");

        result
    });

    let result = work.await.unwrap();

    heartbeat.await.unwrap();

    println!("result: {result}");
    println!("total: {:?}", start.elapsed());
}

async fn example_spawn_blocking() {
    println!("\n spawn_blocking");

    let start = Instant::now();

    let heartbeat = tokio::spawn(heartbeat("spawn_blocking"));

    tokio::task::yield_now().await;

    let work = tokio::task::spawn_blocking(|| {
        println!("spawn_blocking: heavy work started");

        let result = heavy_sync_work();

        println!("spawn_blocking: heavy work finished");

        result
    });

    let result = work.await.unwrap();

    heartbeat.await.unwrap();

    println!("result: {result}");
    println!("total: {:?}", start.elapsed());
}

#[tokio::main(flavor = "current_thread")]
async fn main() {
    example_spawn().await;

    example_spawn_blocking().await;
}