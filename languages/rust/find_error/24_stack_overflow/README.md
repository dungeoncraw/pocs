# 24 - Stack Overflow

**Difficulty:** Medium
**Category:** Recursion / memory / runtime crash

## Scenario
A singly linked list of numbers with helpers to build it, count its elements
and sum them. The tests use lists with a million nodes.

## Symptoms
Small lists behave correctly, but with large inputs the test process crashes
abruptly. You see a message like `thread '...' has overflowed its stack`
followed by `SIGABRT`/`SIGSEGV`, instead of a normal assertion failure. The
crash may still appear even after you fix the first place it happens.

## Hints
1. Note the thread name in the crash message to find which test crashed, then
   run that test alone: `cargo test large_list_len_and_sum`.
2. Each recursive call uses stack space. How deep does the recursion go for a
   list of a million nodes, and how big is a test thread's stack?
3. Anything that walks the list node by node can be written as a loop.
4. Rust drops values automatically. Think about what dropping a very long
   chain of `Box`es does, and whether you can take control of it.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Call stack and recursion depth, converting recursion to iteration, `Drop`
implementations, `Option::take`, recursive drop glue.
