/// Appends a "-copy" entry derived from the first element of the list and
/// returns the first element, or `None` when the list is empty.
pub fn append_copy_of_first(items: &mut Vec<String>) -> Option<String> {
    let leader = items.first()?.clone();
    items.push(format!("{}-copy", leader));
    Some(leader.clone())
}

/// Number of entries ending in "-copy".
pub fn count_copies(items: &[String]) -> usize {
    items.iter().filter(|s| s.ends_with("-copy")).count()
}

/// Repeats `append_copy_of_first` a number of times.
pub fn append_many(items: &mut Vec<String>, times: usize) -> usize {
    let mut done = 0;
    for _ in 0..times {
        if append_copy_of_first(items).is_some() {
            done += 1;
        }
    }
    done
}
