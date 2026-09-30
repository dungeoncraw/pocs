# 19 - Closure Capture

**Difficulty:** Medium
**Category:** Ownership / closures / lifetimes

## Scenario
A small toolkit of closure helpers: an adder factory, a stateful counter, a
pipeline of boxed transformation stages, and a function that scales values
using one thread per value.

## Symptoms
`cargo test` does not even run the tests: the crate fails to compile with
errors about closures that "may outlive" something, mentioning borrowed
variables and lifetimes.

## Hints
1. Read the compiler error fully: it says what is borrowed and where the
   closure is going. Compilers often suggest a keyword.
2. Ask yourself: who owns the captured variable when the closure is used
   after the function returns, or on another thread?
3. Closures capture by reference by default. How can you change how a closure
   captures its environment?
4. Once it compiles, check that `FnMut` state really is kept inside the closure.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Closure capture modes, `move` closures, `Fn`/`FnMut`/`FnOnce`, `'static`
bounds on `thread::spawn`, E0373.
