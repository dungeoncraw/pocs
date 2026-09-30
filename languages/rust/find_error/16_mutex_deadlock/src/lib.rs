use std::sync::Mutex;
use std::thread;
use std::time::Duration;

pub struct Account {
    id: u32,
    balance: Mutex<i64>,
}

impl Account {
    pub fn new(id: u32, balance: i64) -> Self {
        Account {
            id,
            balance: Mutex::new(balance),
        }
    }

    pub fn id(&self) -> u32 {
        self.id
    }

    pub fn balance(&self) -> i64 {
        *self.balance.lock().unwrap()
    }
}

#[derive(Debug, PartialEq)]
pub enum TransferError {
    SameAccount,
    InsufficientFunds,
}

/// Atomically moves `amount` from one account to another.
pub fn transfer(from: &Account, to: &Account, amount: i64) -> Result<(), TransferError> {
    if from.id == to.id {
        return Err(TransferError::SameAccount);
    }
    let mut src = from.balance.lock().unwrap();
    // Simulates the latency of writing an audit record.
    thread::sleep(Duration::from_millis(50));
    let mut dst = to.balance.lock().unwrap();
    if *src < amount {
        return Err(TransferError::InsufficientFunds);
    }
    *src -= amount;
    *dst += amount;
    Ok(())
}
