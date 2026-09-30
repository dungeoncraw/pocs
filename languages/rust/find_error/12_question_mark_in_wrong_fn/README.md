# Question Mark Error Mismatch

- **Difficulty:** Medium
- **Category:** Compile error / error handling

## Scenario

Sums comma-separated integers and rejects negative values, using two helper functions that fail with different error types.

## Symptoms

`cargo test` fails to compile: the `?` operator cannot convert one error type into the function's return error type, with a message mentioning the `From` trait.

## Hints

1. Read which two types the compiler says it cannot convert between.
2. `?` calls `From::from` on the error before returning it early.
3. Your error enum needs a way to be built from the other error type, keeping what the tests expect (see the Display tests).

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- The `?` operator desugaring
- `From` trait and error conversion
- Custom error enums
