use challenge_03_use_after_move::*;

fn names() -> Vec<String> {
    vec!["  Alice ".to_string(), "BOB".to_string(), "carol".to_string()]
}

#[test]
fn process_returns_summary_and_count() {
    let (line, count) = process(names());
    assert_eq!(line, "alice, bob, carol");
    assert_eq!(count, 3);
}

#[test]
fn process_empty() {
    assert_eq!(process(vec![]), (String::new(), 0));
}

#[test]
fn longest_name() {
    let n = normalize(names());
    assert_eq!(longest(&n).map(|s| s.as_str()), Some("carol"));
}
