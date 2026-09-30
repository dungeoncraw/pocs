use challenge_07_missing_lifetime::*;

#[test]
fn longest_picks_longer() {
    assert_eq!(longest("abc", "abcd"), "abcd");
    assert_eq!(longest("abcde", "ab"), "abcde");
}

#[test]
fn longest_tie_prefers_first() {
    let a = String::from("aaa");
    let b = String::from("bbb");
    assert_eq!(longest(&a, &b), "aaa");
}

#[test]
fn longest_result_outlives_one_scope() {
    let outer = String::from("outer string");
    let result;
    {
        let inner = String::from("in");
        result = longest(outer.as_str(), inner.as_str()).len();
    }
    assert_eq!(result, 12);
}

#[test]
fn first_long_word_found_and_default() {
    assert_eq!(first_long_word("a bb cccc dd", 3, "none"), "cccc");
    assert_eq!(first_long_word("a bb", 3, "none"), "none");
}

#[test]
fn longest_line_works() {
    let text = "one\nthree\ntwo\nfour4";
    assert_eq!(longest_line(text), "three");
    assert_eq!(longest_line(""), "");
}
