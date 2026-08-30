use tokio::sync::broadcast;
use tokio::sync::oneshot;
use tokio::sync::watch;

#[derive(Debug, Clone, PartialEq, Eq)]
struct MessageServed {
    id: u64,
    text: String,
}

#[tokio::main]
async fn main() {
    let (event_tx, _) = broadcast::channel::<MessageServed>(16);

    let mut analytics_listener = event_tx.subscribe();
    let mut log_listener = event_tx.subscribe();
    let mut notification_listener = event_tx.subscribe();

    let analytics = tokio::spawn(async move {
        while let Ok(event) = analytics_listener.recv().await {
            println!(
                "[analytics] message {} was served",
                event.id
            );
        }
    });

    let logger = tokio::spawn(async move {
        while let Ok(event) = log_listener.recv().await {
            println!(
                "[log] {:?}",
                event
            );
        }
    });

    let notification = tokio::spawn(async move {
        while let Ok(event) = notification_listener.recv().await {
            println!(
                "[notification] user received: {}",
                event.text
            );
        }
    });

    let messages = vec![
        MessageServed {
            id: 1,
            text: "Keep moving forward".into(),
        },
        MessageServed {
            id: 2,
            text: "Small steps still count".into(),
        },
        MessageServed {
            id: 3,
            text: "Consistency beats intensity".into(),
        },
    ];

    for message in messages {
        event_tx.send(message).unwrap();
    }

    drop(event_tx);

    analytics.await.unwrap();
    logger.await.unwrap();
    notification.await.unwrap();

    let (tx, mut rx) = tokio::sync::mpsc::channel(32);

    tx.send("message 1").await.unwrap();
    tx.send("message 2").await.unwrap();
    drop(tx);

    while let Some(message) = rx.recv().await {
        println!("{message}");
    }

    let (response_tx, response_rx) = oneshot::channel();

    tokio::spawn(async move {
        let result = "message created";

        response_tx.send(result).unwrap();
    });

    let response = response_rx.await.unwrap();

    println!("{response}");

    let (status_tx, mut status_rx) =
        watch::channel("starting");

    let status_handle = tokio::spawn(async move {
        while status_rx.changed().await.is_ok() {
            println!("status: {}", *status_rx.borrow());
        }
    });

    status_tx.send("ready").unwrap();
    status_tx.send("running").unwrap();
    status_tx.send("stopping").unwrap();
    drop(status_tx);
    let _ = status_handle.await;
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_message_served_creation_and_equality() {
        let msg1 = MessageServed {
            id: 1,
            text: "Keep moving forward".into(),
        };
        let msg2 = msg1.clone();

        assert_eq!(msg1.id, 1);
        assert_eq!(msg1.text, "Keep moving forward");
        assert_eq!(msg1, msg2);
        assert_eq!(
            format!("{msg1:?}"),
            "MessageServed { id: 1, text: \"Keep moving forward\" }"
        );
    }

    #[tokio::test]
    async fn test_broadcast_channel_multiple_subscribers() {
        let (event_tx, _) = broadcast::channel::<MessageServed>(16);

        let mut analytics_listener = event_tx.subscribe();
        let mut log_listener = event_tx.subscribe();
        let mut notification_listener = event_tx.subscribe();

        let analytics_task = tokio::spawn(async move {
            let mut received = Vec::new();
            while let Ok(event) = analytics_listener.recv().await {
                received.push(event);
            }
            received
        });

        let log_task = tokio::spawn(async move {
            let mut received = Vec::new();
            while let Ok(event) = log_listener.recv().await {
                received.push(event);
            }
            received
        });

        let notification_task = tokio::spawn(async move {
            let mut received = Vec::new();
            while let Ok(event) = notification_listener.recv().await {
                received.push(event);
            }
            received
        });

        let messages = vec![
            MessageServed {
                id: 1,
                text: "Keep moving forward".into(),
            },
            MessageServed {
                id: 2,
                text: "Small steps still count".into(),
            },
            MessageServed {
                id: 3,
                text: "Consistency beats intensity".into(),
            },
        ];

        for msg in &messages {
            let send_count = event_tx.send(msg.clone()).unwrap();
            assert_eq!(send_count, 3);
        }

        drop(event_tx);

        let analytics_res = analytics_task.await.unwrap();
        let log_res = log_task.await.unwrap();
        let notification_res = notification_task.await.unwrap();

        assert_eq!(analytics_res, messages);
        assert_eq!(log_res, messages);
        assert_eq!(notification_res, messages);
    }

    #[tokio::test]
    async fn test_mpsc_channel_single_producer() {
        let (tx, mut rx) = tokio::sync::mpsc::channel(32);

        tx.send("message 1").await.unwrap();
        tx.send("message 2").await.unwrap();
        drop(tx);

        let mut received = Vec::new();
        while let Some(message) = rx.recv().await {
            received.push(message);
        }

        assert_eq!(received, vec!["message 1", "message 2"]);
    }

    #[tokio::test]
    async fn test_mpsc_channel_multiple_producers() {
        let (tx, mut rx) = tokio::sync::mpsc::channel(32);

        let tx1 = tx.clone();
        let tx2 = tx.clone();
        drop(tx);

        let handle1 = tokio::spawn(async move {
            tx1.send("from producer 1").await.unwrap();
        });

        let handle2 = tokio::spawn(async move {
            tx2.send("from producer 2").await.unwrap();
        });

        handle1.await.unwrap();
        handle2.await.unwrap();

        let mut received = Vec::new();
        while let Some(msg) = rx.recv().await {
            received.push(msg);
        }

        assert_eq!(received.len(), 2);
        assert!(received.contains(&"from producer 1"));
        assert!(received.contains(&"from producer 2"));
    }

    #[tokio::test]
    async fn test_oneshot_channel_success() {
        let (response_tx, response_rx) = oneshot::channel();

        let handle = tokio::spawn(async move {
            let result = "message created";
            response_tx.send(result).unwrap();
        });

        let response = response_rx.await.unwrap();
        handle.await.unwrap();

        assert_eq!(response, "message created");
    }

    #[tokio::test]
    async fn test_oneshot_channel_sender_dropped() {
        let (response_tx, response_rx) = oneshot::channel::<&str>();

        tokio::spawn(async move {
            drop(response_tx);
        });

        let result = response_rx.await;
        assert!(result.is_err());
    }

    #[tokio::test]
    async fn test_watch_channel_state_updates() {
        let (status_tx, mut status_rx) = watch::channel("starting");

        assert_eq!(*status_rx.borrow(), "starting");

        status_tx.send("ready").unwrap();
        assert!(status_rx.changed().await.is_ok());
        assert_eq!(*status_rx.borrow_and_update(), "ready");

        status_tx.send("running").unwrap();
        assert!(status_rx.changed().await.is_ok());
        assert_eq!(*status_rx.borrow_and_update(), "running");

        status_tx.send("stopping").unwrap();
        assert!(status_rx.changed().await.is_ok());
        assert_eq!(*status_rx.borrow_and_update(), "stopping");

        drop(status_tx);
        assert!(status_rx.changed().await.is_err());
    }
}