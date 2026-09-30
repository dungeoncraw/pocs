//! Small helpers for picking the "best" piece of text out of several inputs.

/// Returns the longer of two strings. On a tie, the first one wins.
pub fn longest(a: &str, b: &str) -> &str {
    if b.len() > a.len() {
        b
    } else {
        a
    }
}

/// Returns the first word of `text` that is longer than `min_len`,
/// falling back to `default` when there is none.
pub fn first_long_word(text: &str, min_len: usize, default: &str) -> &str {
    for word in text.split_whitespace() {
        if word.len() > min_len {
            return word;
        }
    }
    default
}

/// Picks the longest line from a block of text (empty string for empty text).
pub fn longest_line(text: &str) -> &str {
    let mut best = "";
    for line in text.lines() {
        best = longest(best, line);
    }
    best
}
