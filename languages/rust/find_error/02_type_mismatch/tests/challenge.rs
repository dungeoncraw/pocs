use challenge_02_type_mismatch::*;

#[test]
fn money_is_formatted() {
    assert_eq!(format_money(1999), "$19.99");
    assert_eq!(format_money(5), "$0.05");
}

#[test]
fn names_are_capitalized() {
    assert_eq!(display_name("widget"), "Widget");
    assert_eq!(display_name(""), "");
}

#[test]
fn invoice_line_is_built() {
    assert_eq!(invoice_line("widget", 3, 1000), "Widget x3 = $30.00");
}

#[test]
fn invoice_total_sums_lines() {
    assert_eq!(invoice_total(&[(2, 250), (1, 1000)]), 1500);
}
