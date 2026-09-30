# 17 - Returning a reference to a local

- **Difficulty:** Medium
- **Category:** Ownership / lifetimes

## Scenario
A few string helpers build new text (a slug, a joined path, a ticket label) and hand it back to the caller.

## Symptoms
`cargo test` fails at compile time, before any test runs. The compiler reports errors with codes such as E0106 ("missing lifetime specifier") and E0515 ("cannot return reference to local variable"), pointing at the functions in `src/lib.rs`.

## Hints
1. Read each error code with `rustc --explain E0106` and `rustc --explain E0515`.
2. Where does the text live after the function returns? Who owns it?
3. Adding a lifetime annotation would not help for the values that are created inside the function.
4. Think about what the function signature should promise about ownership when the data is newly created.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Ownership vs borrowing, lifetime elision rules, dangling references, `String` vs `&str`, `Cow`.
