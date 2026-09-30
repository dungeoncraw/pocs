use std::sync::{Arc, Mutex};

pub type Log = Arc<Mutex<Vec<String>>>;

pub fn new_log() -> Log {
    Arc::new(Mutex::new(Vec::new()))
}

fn record(log: &Log, msg: &str) {
    log.lock().unwrap().push(msg.to_string());
}

/// Holds the table lock while alive.
pub struct TableLock {
    log: Log,
}

impl TableLock {
    pub fn acquire(log: &Log) -> TableLock {
        record(log, "lock");
        TableLock { log: log.clone() }
    }
}

impl Drop for TableLock {
    fn drop(&mut self) {
        record(&self.log, "unlock");
    }
}

/// Commits on drop if `commit()` was called, otherwise rolls back.
pub struct Transaction {
    log: Log,
    committed: bool,
}

impl Transaction {
    pub fn begin(log: &Log) -> Transaction {
        record(log, "begin");
        Transaction { log: log.clone(), committed: false }
    }

    pub fn commit(&mut self) {
        self.committed = true;
    }
}

impl Drop for Transaction {
    fn drop(&mut self) {
        record(&self.log, if self.committed { "commit" } else { "rollback" });
    }
}

/// A locked transaction: the lock must be held for the whole life of the transaction.
pub struct Session {
    lock: TableLock,
    tx: Transaction,
}

impl Session {
    pub fn open(log: &Log) -> Session {
        let lock = TableLock::acquire(log);
        let tx = Transaction::begin(log);
        Session { lock, tx }
    }

    pub fn commit(&mut self) {
        self.tx.commit();
    }
}

pub fn transfer(log: &Log, amount: i64, balance: i64) -> Result<(), String> {
    let _ = TableLock::acquire(log);
    let mut tx = Transaction::begin(log);
    record(log, "debit");
    if amount > balance {
        return Err(format!("insufficient funds: {amount} > {balance}"));
    }
    record(log, "credit");
    tx.commit();
    Ok(())
}
