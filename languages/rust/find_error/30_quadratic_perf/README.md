# Accidentally Quadratic

**Difficulty:** Hard
**Category:** Performance

## Scenario

An ingestion pipeline removes duplicate event ids (keeping first occurrences) and splits the events into batches. It must handle hundreds of thousands of events.

## Symptoms

Small-input tests pass instantly, but tests with 200,000 events fail with a message that the work took longer than a few seconds. Results, when they finish, are correct.

## Hints

1. Correctness is fine; the tests are measuring scaling. Try running a smaller n and doubling it to see how the time grows.
2. Look up the cost of `Vec::contains` and `Vec::remove(0)`, and multiply by the number of times each is called.
3. Which standard data structures give constant-time membership tests? How can you get batches without shifting elements?

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

Big-O analysis, Vec vs HashSet vs VecDeque, memmove cost, `chunks`, profiling in debug vs release.
