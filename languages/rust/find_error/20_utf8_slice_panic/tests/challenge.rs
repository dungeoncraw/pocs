use challenge_20_utf8_slice_panic::*;

#[test]
fn truncate_ascii() {
    assert_eq!(truncate("hello world", 5), "hello...");
    assert_eq!(truncate("hi", 5), "hi");
}

#[test]
fn truncate_counts_characters_not_bytes() {
    assert_eq!(truncate("héllo wörld", 5), "héllo...");
    assert_eq!(truncate("héllo", 5), "héllo");
}

#[test]
fn truncate_handles_emoji() {
    assert_eq!(truncate("🦀🦀🦀🦀🦀", 3), "🦀🦀🦀...");
    assert_eq!(truncate("日本語のテキスト", 4), "日本語の...");
    assert_eq!(truncate("🦀🦀", 2), "🦀🦀");
}

#[test]
fn capitalize_ascii() {
    assert_eq!(capitalize("rust"), "Rust");
    assert_eq!(capitalize("RUST"), "Rust");
    assert_eq!(capitalize(""), "");
}

#[test]
fn capitalize_accented_words() {
    assert_eq!(capitalize("émile"), "Émile");
    assert_eq!(capitalize("ÉCOLE"), "École");
}

#[test]
fn title_case_mixed_text() {
    assert_eq!(title_case("the quick brown fox"), "The Quick Brown Fox");
    assert_eq!(title_case("élan vital über"), "Élan Vital Über");
}
