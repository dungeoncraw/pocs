# 14 - Rc cycle leak

- **Difficulty:** Medium
- **Category:** Memory management / reference counting

## Scenario
A tree of `Node`s where parents own their children and each child can navigate back to its parent (for example to build a path such as `root/usr/bin`). Each node bumps a shared counter when it is destroyed so we can observe cleanup.

## Symptoms
`cargo test` compiles and some tests pass, but others report wrong reference counts, nodes that never get dropped (the destructor counter stays at 0) and children that still see a parent that should be gone. No panic backtrace points to a bad line; things simply are not freed.

## Hints
1. Rust considers leaks memory-safe. Nothing will warn you: look at `Rc::strong_count` values.
2. Draw the ownership arrows between a parent and a child. Do you see a loop?
3. `Drop` only runs when the strong count reaches zero.
4. Look at `std::rc::Weak` and how to get a usable reference back out of it.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
`Rc` vs `Weak`, reference cycles, `Drop`, `Rc::strong_count`, `Weak::upgrade`.
