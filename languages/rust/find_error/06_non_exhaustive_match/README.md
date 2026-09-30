# 06 - Non-Exhaustive Match

- **Difficulty:** Easy
- **Category:** Compile error / pattern matching

## Scenario
An order-tracking module models the lifecycle of an order with an enum and provides labels, cancellation rules and state transitions.

## Symptoms
`cargo test` fails to compile with an error about "non-exhaustive patterns", naming a pattern that is not covered.

## Hints
1. The error tells you exactly which value is not handled.
2. Every `match` must cover all variants of the enum.
3. Look at the tests to see what behavior is expected for the missing case, rather than silencing the compiler with a catch-all.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Exhaustive pattern matching
- Enums
- Wildcard `_` arms and their trade-offs (E0004)
