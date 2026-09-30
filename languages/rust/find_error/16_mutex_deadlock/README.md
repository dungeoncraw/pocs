# 16 - Mutex deadlock

- **Difficulty:** Medium
- **Category:** Concurrency / deadlock

## Scenario
A minimal bank: each `Account` protects its balance with a `Mutex`, and `transfer` moves money between two accounts so that both updates happen atomically. Several threads may transfer at the same time, including in opposite directions between the same pair of accounts.

## Symptoms
Single-threaded tests pass. The concurrent test does not complete: after a few seconds it fails with a message saying the transfer did not finish in time (a deadlock, so nothing ever panics inside the library).

## Hints
1. Write down, for two threads doing A->B and B->A, which lock each holds and which it waits for.
2. This is the classic "circular wait" condition. Which of the four deadlock conditions is easiest to remove here?
3. If every thread acquired locks following the same global rule, could the circle form?
4. Something already identifies each account uniquely.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Deadlock conditions, lock ordering, `MutexGuard` lifetimes, timeouts for testing concurrent code.
