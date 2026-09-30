# 02 - Type Mismatch

- **Difficulty:** Easy
- **Category:** Compile error / types

## Scenario
A small billing module formats money, capitalizes item names and builds invoice lines and totals from quantities and unit prices in cents.

## Symptoms
`cargo test` fails to build. The compiler reports "mismatched types" errors, with "expected ... found ..." notes in more than one place.

## Hints
1. Look at the "expected X, found Y" pairs in each error.
2. Rust never converts between integer types or between `&str` and `String` implicitly.
3. Check which types are being multiplied, and what a `match` arm's type must be.
4. Study `as`, `From`/`Into` and `to_string()` / `to_owned()`.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Integer types and conversions
- `&str` vs `String`
- Match arm type unification (E0308)
