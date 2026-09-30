/// Trims and lowercases every name.
pub fn normalize(names: Vec<String>) -> Vec<String> {
    names.into_iter().map(|n| n.trim().to_lowercase()).collect()
}

/// Joins names into a single comma-separated line.
pub fn summarize(names: &Vec<String>) -> String {
    names.join(", ")
}

/// Returns the summary line and how many names were processed.
pub fn process(names: Vec<String>) -> (String, usize) {
    let cleaned = normalize(names);
    let line = summarize(&cleaned);
    let count = cleaned.len();
    (line, count)
}

/// Returns the longest name, if any.
pub fn longest(names: &[String]) -> Option<&String> {
    names.iter().max_by_key(|n| n.len())
}
