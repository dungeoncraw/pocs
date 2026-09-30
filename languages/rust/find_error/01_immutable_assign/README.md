# 01 - Immutable Assign

- **Difficulty:** Easy
- **Category:** Compile error / mutability

## Scenario
A tiny checkout library computes running totals of item prices, clamps a discount percentage to at most 100, and applies it to the cart total.

## Symptoms
`cargo test` does not even run the tests: the crate fails to compile and the compiler reports errors about assigning more than once / to something that is not allowed to change.

## Hints
1. Read the compiler output carefully; it points at the exact lines and explains the rule that was broken.
2. In Rust, bindings and function parameters are read-only by default.
3. The compiler usually suggests a way to change how something is declared.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Mutability (`let` vs `let mut`)
- Mutable function parameters
- Reading rustc error messages (E0384)
