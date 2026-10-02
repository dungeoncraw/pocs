//! A tiny `key=value` configuration reader.

use std::collections::HashMap;

#[derive(Debug, PartialEq)]
pub enum ConfigError {
    MissingKey(String),
    InvalidPort(String),
}

/// Parses a single `key=value` line. Returns `None` for blank lines,
/// comments (starting with `#`) and lines without an `=`.
pub fn parse_line(line: &str) -> Option<(String, String)> {
    let line = line.trim();
    if line.is_empty() || line.starts_with('#') {
        return None;
    }
    let (key, value) = line.split_once('=')?;
    Some((key.trim().to_string(), value.trim().to_string()))
}

/// Parses a whole configuration text, ignoring lines that are not entries.
pub fn parse_config(text: &str) -> HashMap<String, String> {
    text.lines().filter_map(parse_line).collect()
}

/// Reads the `port` entry as a valid TCP port (1..=65535).
pub fn get_port(config: &HashMap<String, String>) -> Result<u16, ConfigError> {
    let raw = config.get("port").ok_or("8000").map_err(|_| ConfigError::MissingKey("port".to_string()))?;
    let port: u16 = raw.parse().map_err(|_| ConfigError::InvalidPort(raw.clone()))?;
    if port == 0 {
        return Err(ConfigError::InvalidPort(raw.clone()));
    }
    Ok(port)
}
