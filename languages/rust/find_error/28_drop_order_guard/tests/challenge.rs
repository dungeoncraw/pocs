use challenge_28_drop_order_guard::*;

fn events(log: &Log) -> Vec<String> {
    log.lock().unwrap().clone()
}

#[test]
fn successful_transfer_commits_inside_the_lock() {
    let log = new_log();
    assert_eq!(transfer(&log, 10, 100), Ok(()));
    assert_eq!(events(&log), ["lock", "begin", "debit", "credit", "commit", "unlock"]);
}

#[test]
fn failed_transfer_rolls_back_inside_the_lock() {
    let log = new_log();
    assert!(transfer(&log, 500, 100).is_err());
    assert_eq!(events(&log), ["lock", "begin", "debit", "rollback", "unlock"]);
}

#[test]
fn session_commits_before_releasing_lock() {
    let log = new_log();
    {
        let mut s = Session::open(&log);
        s.commit();
    }
    assert_eq!(events(&log), ["lock", "begin", "commit", "unlock"]);
}

#[test]
fn uncommitted_session_rolls_back_before_releasing_lock() {
    let log = new_log();
    drop(Session::open(&log));
    assert_eq!(events(&log), ["lock", "begin", "rollback", "unlock"]);
}
