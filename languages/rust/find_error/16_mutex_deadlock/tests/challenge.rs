use challenge_16_mutex_deadlock::*;
use std::sync::{mpsc, Arc, Barrier};
use std::thread;
use std::time::Duration;

#[test]
fn simple_transfer_moves_money() {
    let a = Account::new(1, 100);
    let b = Account::new(2, 50);
    transfer(&a, &b, 30).unwrap();
    assert_eq!(a.balance(), 70);
    assert_eq!(b.balance(), 80);
}

#[test]
fn transfer_rejects_bad_requests() {
    let a = Account::new(1, 10);
    let b = Account::new(2, 0);
    assert_eq!(transfer(&a, &b, 11), Err(TransferError::InsufficientFunds));
    assert_eq!(transfer(&a, &a, 1), Err(TransferError::SameAccount));
    assert_eq!((a.balance(), b.balance()), (10, 0));
}

#[test]
fn opposite_transfers_do_not_deadlock() {
    let a = Arc::new(Account::new(1, 100));
    let b = Arc::new(Account::new(2, 100));
    let barrier = Arc::new(Barrier::new(2));
    let (tx, rx) = mpsc::channel();

    let mut handles = Vec::new();
    for (from, to, amount) in [(a.clone(), b.clone(), 30), (b.clone(), a.clone(), 10)] {
        let barrier = barrier.clone();
        let tx = tx.clone();
        handles.push(thread::spawn(move || {
            barrier.wait();
            let r = transfer(&from, &to, amount);
            tx.send(r).unwrap();
        }));
    }
    drop(tx);

    for _ in 0..2 {
        let result = rx
            .recv_timeout(Duration::from_secs(3))
            .expect("transfer did not finish in time: deadlock?");
        assert_eq!(result, Ok(()));
    }
    for h in handles {
        h.join().unwrap();
    }
    assert_eq!(a.balance(), 80);
    assert_eq!(b.balance(), 120);
}
