use challenge_12_question_mark_in_wrong_fn::*;

#[test]
fn sums_valid_input() {
    assert_eq!(sum_numbers("1, 2,3 ,4"), Ok(10));
}

#[test]
fn empty_input_is_zero() {
    assert_eq!(sum_numbers(""), Ok(0));
    assert_eq!(sum_numbers("1,,2,"), Ok(3));
}

#[test]
fn negative_is_rejected() {
    assert_eq!(sum_numbers("5,-3,2"), Err(SumError::Negative(-3)));
}

#[test]
fn bad_token_reports_parse_error() {
    assert!(matches!(sum_numbers("1,abc,3"), Err(SumError::Parse(_))));
}

#[test]
fn parse_error_message_mentions_token_kind() {
    let err = sum_numbers("x").unwrap_err();
    assert!(err.to_string().starts_with("cannot parse"));
}
