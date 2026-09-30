# 23 - Channel Never Closes

**Difficulty:** Medium
**Category:** Concurrency / channels

## Scenario
A tiny worker pool: each chunk of numbers is processed on its own thread and
the partial results are sent back over an `mpsc` channel to be combined.

## Symptoms
`cargo test` seems stuck: the tests never finish on their own. The tests are
protected by a timeout, so they fail with a "timed out" message instead of
hanging forever. All workers do finish their job, yet the function never returns.

## Hints
1. When does a `for x in rx` loop (or `rx.iter()`) stop?
2. Count how many `Sender`s exist. Who owns each one, and when is each dropped?
3. Workers drop their sender when they end. Is anyone else still holding one?
4. Think about the empty-input case too: nobody ever sends anything.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
`mpsc` channel closing semantics, `Sender::clone`, `drop`, RAII and scope,
receiver iteration.
