# Drop Order of Guards

**Difficulty:** Hard
**Category:** RAII / Drop

## Scenario

A ledger uses a `TableLock` and a `Transaction` guard. A transaction must always commit or roll back while the lock is still held, and the lock must be released last. Guards log their events to a shared log.

## Symptoms

Tests fail on the exact order of recorded events: the lock appears to be released at the wrong time, for instance before the work is even done, or before the transaction finishes.

## Hints

1. Print the event log in the failing assertions and compare it to the expected sequence; look at where the first difference occurs.
2. When exactly is a value bound to `_` dropped, compared to one bound to a named variable?
3. Look at the order in which struct fields are dropped, versus the order in which locals are dropped.

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

Drop timing, `let _ = x` vs `let _x = x`, drop order of locals (reverse) and struct fields (declaration order), RAII.
