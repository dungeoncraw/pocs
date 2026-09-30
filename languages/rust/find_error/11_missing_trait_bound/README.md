# Missing Trait Bound

- **Difficulty:** Easy
- **Category:** Compile error / generics

## Scenario

Generic helpers over slices: find the largest item, describe all items as text, and build a report string.

## Symptoms

`cargo test` fails to compile with several errors: binary operation cannot be applied, cannot move out of a reference, and method `to_string` exists but its trait bounds are not satisfied.

## Hints

1. Read every error; each one names an operation the generic type `T` is not known to support.
2. A generic `T` can only do what its bounds allow. Which capabilities do comparison, copying out of a slice and formatting need?
3. Fixing one function may reveal that the caller needs the same promises. Use the `T: A + B` or `where` syntax.

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- Trait bounds and `where` clauses
- `PartialOrd`, `Copy`, `Display`
- The Rust Book, chapter 10.2
