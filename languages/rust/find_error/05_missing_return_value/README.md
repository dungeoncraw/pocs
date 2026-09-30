# 05 - Missing Return Value

- **Difficulty:** Easy
- **Category:** Compile error / expressions

## Scenario
A shipping calculator charges a base rate up to a weight limit and a per-100g surcharge above it, with free shipping for big orders.

## Symptoms
`cargo test` fails to compile with a "mismatched types" error saying it expected an integer but found `()`.

## Hints
1. The compiler often highlights the branch or function whose value is `()` and may add a note about a semicolon.
2. In Rust, `if`/`else` and blocks are expressions; the last expression without a semicolon is the block's value.
3. Compare how the two branches of the `if` end.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Expressions vs statements
- The unit type `()`
- Implicit return values of blocks
