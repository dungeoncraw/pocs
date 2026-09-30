/// Shortens `text` to at most `max_chars` characters, adding "..." when cut.
pub fn truncate(text: &str, max_chars: usize) -> String {
    if text.len() <= max_chars {
        return text.to_string();
    }
    let mut out = text[..max_chars].to_string();
    out.push_str("...");
    out
}

/// Upper-cases the first letter of a word and lower-cases the rest.
pub fn capitalize(word: &str) -> String {
    if word.is_empty() {
        return String::new();
    }
    let first = &word[..1];
    let rest = &word[1..];
    format!("{}{}", first.to_uppercase(), rest.to_lowercase())
}

/// Builds a title-cased version of a sentence.
pub fn title_case(sentence: &str) -> String {
    sentence
        .split_whitespace()
        .map(capitalize)
        .collect::<Vec<_>>()
        .join(" ")
}
