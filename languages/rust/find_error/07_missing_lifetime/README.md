# Missing Lifetime

- **Difficulty:** Easy
- **Category:** Compile error / lifetimes

## Scenario

A small text-picking library: choose the longer of two strings, the first long word in a text, and the longest line of a block of text.

## Symptoms

`cargo test` does not even start running tests; the build fails with an error code starting with E0106 and a message about a missing lifetime specifier.

## Hints

1. Read the compiler message: it says what it cannot figure out about the returned reference.
2. When a function returns a reference, the compiler must know which input(s) it may borrow from.
3. Run `rustc --explain E0106` and check what elision rules apply with several reference parameters.

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- Lifetime annotations
- Lifetime elision rules
- The Rust Book, chapter 10.3
