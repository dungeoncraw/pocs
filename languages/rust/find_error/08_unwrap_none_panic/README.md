# Unwrap on None

- **Difficulty:** Easy
- **Category:** Runtime panic / error handling

## Scenario

A tiny `key=value` config reader. Malformed lines should be ignored, and reading the port should report missing or invalid values as errors instead of crashing.

## Symptoms

Several tests fail with panics such as `called `Option::unwrap()` on a `None` value` or `called `Result::unwrap()` on an `Err` value`.

## Hints

1. Run `RUST_BACKTRACE=1 cargo test` to find the exact line of each panic.
2. Look at what the function signatures promise: `Option` and `Result`. Are the promises kept?
3. Think about what value should be returned when the input is missing or invalid, and how `?`, `ok_or`, `map_err` help.

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- Option and Result
- `?` operator, `ok_or`, `map_err`
- Why library code should avoid `unwrap`
