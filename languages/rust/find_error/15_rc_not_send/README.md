# 15 - Rc is not Send

- **Difficulty:** Medium
- **Category:** Concurrency / trait bounds (Send, Sync)

## Scenario
`SharedCounter` is a cloneable counter handle meant to be shared among worker threads. `count_in_parallel` spawns workers that increment it, and callers should be able to do the same with their own threads.

## Symptoms
`cargo test` does not even get to run the tests: the build fails with a long compiler error mentioning that something "cannot be sent between threads safely" and a missing `Send` implementation.

## Hints
1. Read the whole error, especially the "required by a bound in `std::thread::spawn`" and "within ... the trait `Send` is not implemented" notes.
2. Find which field makes the struct non-`Send`.
3. Compare the thread-safe and non-thread-safe counterparts of both smart-pointer types used in the field.
4. Non-atomic shared mutation across threads is a data race; something must synchronise access.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
`Send` and `Sync` auto traits, `Rc` vs `Arc`, `RefCell` vs `Mutex`, `std::thread::spawn` bounds.
