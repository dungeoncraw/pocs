use std::sync::Mutex;
use std::thread;
use std::time::Duration;

#[derive(Debug, PartialEq, Eq)]
pub struct InsufficientFunds {
    pub requested: i64,
    pub available: i64,
}

pub struct Account {
    balance: Mutex<i64>,
}

// Stand-in for the audit-log write every ledger operation performs.
fn audit_latency() {
    thread::sleep(Duration::from_millis(2));
}

impl Account {
    pub fn new(initial: i64) -> Self {
        Account { balance: Mutex::new(initial) }
    }

    pub fn balance(&self) -> i64 {
        *self.balance.lock().unwrap()
    }

    pub fn deposit(&self, amount: i64) {
        let current = *self.balance.lock().unwrap();
        audit_latency();
        *self.balance.lock().unwrap() = current + amount;
    }

    pub fn withdraw(&self, amount: i64) -> Result<(), InsufficientFunds> {
        let current = *self.balance.lock().unwrap();
        if current < amount {
            return Err(InsufficientFunds { requested: amount, available: current });
        }
        audit_latency();
        *self.balance.lock().unwrap() = current - amount;
        Ok(())
    }
}
