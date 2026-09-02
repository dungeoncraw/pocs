use std::time::Duration;
use tokio::time::sleep;

#[derive(Debug, PartialEq, Eq)]
struct Message {
    id: u64,
    text: String,
}

#[derive(Debug, PartialEq, Eq)]
enum DatabaseError {
    Timeout,
}

async fn find_message(id: u64, delay: Duration) -> Message {
    println!("database: searching for message {id}");

    sleep(delay).await;
    // this line never executes as the timeout is reached first
    println!("database: query finished");

    Message {
        id,
        text: "Message returned".into(),
    }
}

async fn find_message_with_timeout(
    id: u64,
    database_delay: Duration,
    timeout: Duration,
) -> Result<Message, DatabaseError> {
    // this is much closer than Promise.race()
    // the first that returns Ready wins and drop the other
    // but don't drop related tasks started by dropped future
    tokio::select! {
        message = find_message(id, database_delay) => {
            Ok(message)
        }

        _ = sleep(timeout) => {
            Err(DatabaseError::Timeout)
        }
    }
}

#[tokio::main]
async fn main() {
    let result = find_message_with_timeout(
        1,
        Duration::from_secs(2),
        Duration::from_secs(1),
    )
        .await;

    println!("result: {result:?}");
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::sync::atomic::{AtomicBool, Ordering};
    use std::sync::Arc;

    #[tokio::test]
    async fn test_find_message() {
        let msg = find_message(42, Duration::from_millis(1)).await;
        assert_eq!(msg.id, 42);
        assert_eq!(msg.text, "Message returned");
    }

    #[tokio::test]
    async fn test_find_message_with_timeout_success() {
        let result = find_message_with_timeout(
            10,
            Duration::from_millis(10),
            Duration::from_millis(50),
        )
        .await;

        assert_eq!(
            result,
            Ok(Message {
                id: 10,
                text: "Message returned".into(),
            })
        );
    }

    #[tokio::test]
    async fn test_find_message_with_timeout_exceeded() {
        let result = find_message_with_timeout(
            20,
            Duration::from_millis(50),
            Duration::from_millis(10),
        )
        .await;

        assert_eq!(result, Err(DatabaseError::Timeout));
    }

    #[tokio::test]
    async fn test_find_message_with_timeout_zero_delay() {
        let result = find_message_with_timeout(
            30,
            Duration::ZERO,
            Duration::from_millis(50),
        )
        .await;

        assert_eq!(
            result,
            Ok(Message {
                id: 30,
                text: "Message returned".into(),
            })
        );
    }

    #[tokio::test]
    async fn test_find_message_with_timeout_zero_timeout() {
        let result = find_message_with_timeout(
            40,
            Duration::from_millis(50),
            Duration::ZERO,
        )
        .await;

        assert_eq!(result, Err(DatabaseError::Timeout));
    }

    struct DropDetector {
        dropped: Arc<AtomicBool>,
    }
    // this simulates a future that is dropped before it completes
    impl Drop for DropDetector {
        fn drop(&mut self) {
            self.dropped.store(true, Ordering::SeqCst);
        }
    }

    #[tokio::test]
    async fn test_select_drops_losing_future() {
        let dropped = Arc::new(AtomicBool::new(false));
        let detector = DropDetector {
            dropped: Arc::clone(&dropped),
        };

        let losing_future_completed = Arc::new(AtomicBool::new(false));
        let losing_completed_clone = Arc::clone(&losing_future_completed);

        let slow_future = async move {
            let _detector = detector; // moved into this future
            sleep(Duration::from_millis(50)).await;
            losing_completed_clone.store(true, Ordering::SeqCst);
        };

        let fast_future = async {
            sleep(Duration::from_millis(10)).await;
            "fast_won"
        };

        let winner = tokio::select! {
            _ = slow_future => "slow_won",
            res = fast_future => res,
        };

        assert_eq!(winner, "fast_won");

        assert!(
            dropped.load(Ordering::SeqCst),
            "The losing future's resources should be dropped"
        );

        assert!(
            !losing_future_completed.load(Ordering::SeqCst),
            "Execution after the await in the dropped future must not run"
        );
    }

    #[tokio::test]
    async fn test_select_does_not_drop_spawned_background_tasks() {
        let parent_future_dropped = Arc::new(AtomicBool::new(false));
        let detector = DropDetector {
            dropped: Arc::clone(&parent_future_dropped),
        };

        let spawned_task_completed = Arc::new(AtomicBool::new(false));
        let task_completed_clone = Arc::clone(&spawned_task_completed);

        let future_with_spawned_task = async move {
            let _detector = detector;

            // Spawn a detached background task that is not going to drop
            tokio::spawn(async move {
                sleep(Duration::from_millis(30)).await;
                task_completed_clone.store(true, Ordering::SeqCst);
            });


            sleep(Duration::from_millis(100)).await;
        };

        let timeout_future = sleep(Duration::from_millis(10));

        tokio::select! {
            _ = future_with_spawned_task => {
                panic!("future_with_spawned_task should have timed out");
            }
            _ = timeout_future => {
                // Timeout branch wins
            }
        }

        assert!(
            parent_future_dropped.load(Ordering::SeqCst),
            "The parent future in select! should be dropped upon losing"
        );

        assert!(
            !spawned_task_completed.load(Ordering::SeqCst),
            "Spawned task should still be running in background"
        );

        sleep(Duration::from_millis(40)).await;

        assert!(
            spawned_task_completed.load(Ordering::SeqCst),
            "Spawned task started by dropped future must NOT be cancelled/dropped and should run to completion"
        );
    }
}