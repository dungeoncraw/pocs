# Lost Update Race

**Difficulty:** Hard
**Category:** Concurrency

## Scenario

An `Account` behind a `Mutex` supports `deposit` and `withdraw`, used from many threads. Balances must always be exact and never overdrawn.

## Symptoms

Tests that use many threads report a final balance far below what was deposited, and more withdrawals succeed than the funds allow. Single-threaded tests pass.

## Hints

1. The type is fully protected by a Mutex, so why can it still be wrong? Look at how long each lock is actually held.
2. Trace two threads through `deposit` step by step and look at what happens between reading and writing.
3. Think about what must be atomic: the whole read-check-modify-write, not each individual access.

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

Mutex and MutexGuard lifetimes, temporaries dropping at end of statement, check-then-act races, atomicity.
