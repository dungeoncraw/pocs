//! Sums comma-separated integers, rejecting negative values.

use std::fmt;
use std::num::ParseIntError;

#[derive(Debug, PartialEq)]
pub enum SumError {
    Parse(String),
    Negative(i64),
}

impl fmt::Display for SumError {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            SumError::Parse(tok) => write!(f, "cannot parse '{}'", tok),
            SumError::Negative(n) => write!(f, "negative value {}", n),
        }
    }
}

impl From<ParseIntError> for SumError {
    fn from(err: ParseIntError) -> Self {
        SumError::Parse(err.to_string())
    }
}

impl std::error::Error for SumError {}

fn parse_token(token: &str) -> Result<i64, ParseIntError> {
    token.trim().parse::<i64>()
}

fn check_non_negative(n: i64) -> Result<i64, SumError> {
    if n < 0 {
        Err(SumError::Negative(n))
    } else {
        Ok(n)
    }
}

/// Sums all comma-separated tokens. Empty input sums to 0.
pub fn sum_numbers(input: &str) -> Result<i64, SumError> {
    let mut total = 0;
    for token in input.split(',').filter(|t| !t.trim().is_empty()) {
        let n = parse_token(token)?;
        total += check_non_negative(n)?;
    }
    Ok(total)
}
