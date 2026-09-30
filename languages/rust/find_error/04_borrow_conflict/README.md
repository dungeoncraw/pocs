# 04 - Borrow Conflict

- **Difficulty:** Easy
- **Category:** Compile error / borrowing

## Scenario
A list helper appends a "-copy" entry derived from the first element of a list, returning the original first element.

## Symptoms
`cargo test` fails to compile: the compiler complains that it cannot borrow something as mutable because it is also borrowed as immutable, and points at a later use of the earlier borrow.

## Hints
1. The error lists three locations: the first borrow, the conflicting borrow, and the later use that keeps the first alive.
2. A reference stays alive until its last use, not until the end of the scope.
3. Think about whether you can finish using the reference (or copy out what you need) before mutating.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Aliasing XOR mutability
- Non-lexical lifetimes
- E0502
