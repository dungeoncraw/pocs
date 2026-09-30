# NaN in the Leaderboard

**Difficulty:** Hard
**Category:** Panics / Floating point

## Scenario

Leaderboard statistics (ranking, top-N, max, mean, median) over readings. NaN readings are sensor dropouts and must simply be ignored by every statistic.

## Symptoms

Several tests panic with `called `Option::unwrap()` on a `None` value` when the data contains NaN; tests on clean data pass.

## Hints

1. Run with RUST_BACKTRACE=1 to see which line unwraps. What does `partial_cmp` return when one side is NaN?
2. f64 is only PartialOrd, not Ord. Read the docs of `f64::total_cmp` and `f64::is_nan`.
3. Decide where the invalid readings should be removed so that every function, including `mean`, agrees on the same data.

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

PartialOrd vs Ord, NaN semantics, total_cmp, sorting floats, defensive input filtering.
