use std::time::Duration;
use tokio::time::Instant;

async fn example_1() {
    let start = Instant::now();

    tokio::spawn(async move {
        loop {
            println!("heartbeat: {:?}", start.elapsed());
            tokio::time::sleep(Duration::from_millis(500)).await;
        }
    });

    tokio::spawn(async {
        println!("blocking task 1 started");
        std::thread::sleep(Duration::from_secs(5));
        println!("blocking task 1 finished");
    });

    tokio::spawn(async {
        println!("blocking task 2 started");
        std::thread::sleep(Duration::from_secs(5));
        println!("blocking task 2 finished");
    });

    tokio::time::sleep(Duration::from_secs(8)).await;
}

async fn example_2() {
    let start = Instant::now();

    tokio::spawn(async move {
        loop {
            println!("heartbeat: {:?}", start.elapsed());
            tokio::time::sleep(Duration::from_millis(500)).await;
        }
    });

    tokio::spawn(async {
        println!("async task 1 started");
        tokio::time::sleep(Duration::from_secs(5)).await;
        println!("async task 1 finished");
    });

    tokio::spawn(async {
        println!("async task 2 started");
        tokio::time::sleep(Duration::from_secs(5)).await;
        println!("async task 2 finished");
    });

    tokio::time::sleep(Duration::from_secs(8)).await;
}

fn blocking_operation(id: u32) {
    println!("blocking operation {id} started");
    std::thread::sleep(Duration::from_secs(5));
    println!("blocking operation {id} finished");
}
async fn example_3() {
    let start = Instant::now();

    tokio::spawn(async move {
        loop {
            println!("heartbeat: {:?}", start.elapsed());
            tokio::time::sleep(Duration::from_millis(500)).await;
        }
    });

    let task1 = tokio::task::spawn_blocking(|| {
        blocking_operation(1);
    });

    let task2 = tokio::task::spawn_blocking(|| {
        blocking_operation(2);
    });

    task1.await.unwrap();
    task2.await.unwrap();

    tokio::time::sleep(Duration::from_secs(2)).await;
}
#[tokio::main(flavor = "multi_thread", worker_threads = 2)]
async fn main() {
    example_1().await;
    example_2().await;
    example_3().await;
}