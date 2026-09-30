use challenge_08_unwrap_none_panic::*;

#[test]
fn parses_valid_line() {
    assert_eq!(
        parse_line("  host = localhost "),
        Some(("host".to_string(), "localhost".to_string()))
    );
}

#[test]
fn ignores_blank_and_comment_lines() {
    assert_eq!(parse_line(""), None);
    assert_eq!(parse_line("   "), None);
    assert_eq!(parse_line("# port=1"), None);
}

#[test]
fn line_without_equals_is_none() {
    assert_eq!(parse_line("just some text"), None);
}

#[test]
fn config_skips_garbage_lines() {
    let cfg = parse_config("# c\nhost=a\ngarbage\nport=80\n");
    assert_eq!(cfg.len(), 2);
    assert_eq!(cfg["host"], "a");
}

#[test]
fn port_ok() {
    let cfg = parse_config("port=8080");
    assert_eq!(get_port(&cfg), Ok(8080));
}

#[test]
fn port_missing_is_error() {
    let cfg = parse_config("host=a");
    assert_eq!(get_port(&cfg), Err(ConfigError::MissingKey("port".to_string())));
}

#[test]
fn port_invalid_is_error() {
    for bad in ["abc", "70000", "-1", "0"] {
        let cfg = parse_config(&format!("port={bad}"));
        assert_eq!(get_port(&cfg), Err(ConfigError::InvalidPort(bad.to_string())));
    }
}
