use challenge_26_lost_update_race::*;
use std::sync::{Arc, Barrier};
use std::thread;

#[test]
fn sequential_operations_work() {
    let acc = Account::new(100);
    acc.deposit(50);
    assert_eq!(acc.withdraw(120), Ok(()));
    assert_eq!(acc.balance(), 30);
    assert_eq!(acc.withdraw(31), Err(InsufficientFunds { requested: 31, available: 30 }));
}

#[test]
fn concurrent_deposits_are_all_counted() {
    let acc = Arc::new(Account::new(0));
    let barrier = Arc::new(Barrier::new(8));
    let handles: Vec<_> = (0..8)
        .map(|_| {
            let acc = Arc::clone(&acc);
            let barrier = Arc::clone(&barrier);
            thread::spawn(move || {
                barrier.wait();
                for _ in 0..5 {
                    acc.deposit(10);
                }
            })
        })
        .collect();
    for h in handles {
        h.join().unwrap();
    }
    assert_eq!(acc.balance(), 8 * 5 * 10);
}

#[test]
fn concurrent_withdrawals_never_overdraw() {
    let acc = Arc::new(Account::new(100));
    let barrier = Arc::new(Barrier::new(10));
    let handles: Vec<_> = (0..10)
        .map(|_| {
            let acc = Arc::clone(&acc);
            let barrier = Arc::clone(&barrier);
            thread::spawn(move || {
                barrier.wait();
                acc.withdraw(50).is_ok()
            })
        })
        .collect();
    let successes = handles.into_iter().map(|h| h.join().unwrap()).filter(|&ok| ok).count();
    assert_eq!(successes, 2, "only two withdrawals of 50 fit in 100");
    assert_eq!(acc.balance(), 0);
}
