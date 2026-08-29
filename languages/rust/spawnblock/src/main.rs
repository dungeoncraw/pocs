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

#[cfg(test)]
mod tests {
    use super::*;
    use std::sync::atomic::{AtomicUsize, Ordering};
    use std::sync::Arc;

    #[test]
    fn test_heavy_sync_work_result() {
        let result = heavy_sync_work();
        assert_eq!(result, 42);
    }

    #[tokio::test]
    async fn test_heartbeat_completion() {
        tokio::time::pause();
        heartbeat("test_heartbeat").await;
    }

    #[tokio::test(flavor = "current_thread")]
    async fn test_example_spawn_execution() {
        example_spawn().await;
    }

    #[tokio::test(flavor = "current_thread")]
    async fn test_example_spawn_blocking_execution() {
        example_spawn_blocking().await;
    }

    #[tokio::test(flavor = "current_thread")]
    async fn test_spawn_blocking_allows_concurrent_tasks_on_current_thread() {
        let counter = Arc::new(AtomicUsize::new(0));
        let counter_clone = Arc::clone(&counter);

        let ticker = tokio::spawn(async move {
            for _ in 0..5 {
                counter_clone.fetch_add(1, Ordering::SeqCst);
                tokio::time::sleep(Duration::from_millis(50)).await;
            }
        });

        tokio::task::yield_now().await;

        let blocking_job = tokio::task::spawn_blocking(|| {
            thread::sleep(Duration::from_millis(150));
            100
        });

        let job_result = blocking_job.await.unwrap();
        ticker.await.unwrap();

        assert_eq!(job_result, 100);
        assert_eq!(counter.load(Ordering::SeqCst), 5);
    }

    #[tokio::test(flavor = "current_thread")]
    async fn test_blocking_in_spawn_starves_current_thread() {
        let counter = Arc::new(AtomicUsize::new(0));
        let counter_clone = Arc::clone(&counter);

        let ticker = tokio::spawn(async move {
            for _ in 0..5 {
                counter_clone.fetch_add(1, Ordering::SeqCst);
                tokio::time::sleep(Duration::from_millis(50)).await;
            }
        });

        tokio::task::yield_now().await;

        let blocking_in_spawn = tokio::spawn(async {
            thread::sleep(Duration::from_millis(200));
            200
        });

        let job_result = blocking_in_spawn.await.unwrap();
        ticker.await.unwrap();

        assert_eq!(job_result, 200);
        assert_eq!(counter.load(Ordering::SeqCst), 5);
    }
}