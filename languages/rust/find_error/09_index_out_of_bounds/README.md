# Index Out of Bounds

- **Difficulty:** Easy
- **Category:** Runtime panic / slices

## Scenario

Sliding-window statistics over sensor readings: a moving average and a per-window maximum.

## Symptoms

Some tests panic with `index out of bounds: the len is N but the index is N`; others pass.

## Hints

1. Use `RUST_BACKTRACE=1 cargo test` and note which test and which line panics.
2. Work through the smallest failing example by hand, writing down the values of the loop variable.
3. Compare how many windows exist with how many iterations the loop performs. `dbg!` can help.

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- Slice indexing and ranges (`..` vs `..=`)
- Off-by-one errors
- `slice::windows`
