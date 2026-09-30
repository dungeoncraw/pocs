# 03 - Use After Move

- **Difficulty:** Easy
- **Category:** Compile error / ownership

## Scenario
A helper library normalizes a list of names, joins them into a summary line, and reports how many names were processed.

## Symptoms
`cargo test` fails to compile with an error saying a value was "borrowed after move" or "used after move", with notes pointing at where it was moved.

## Hints
1. The compiler points at both the place where the value moved and the place where it is used again.
2. Passing a `Vec<String>` by value transfers ownership to the callee.
3. Consider whether the callee really needs to own the data, or whether you can get what you need before the move.
4. Study references (`&`) and `.clone()` and when each is appropriate.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
- Ownership and move semantics
- Borrowing vs cloning
- E0382
