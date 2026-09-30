# 21 - Mutate While Iterating

**Difficulty:** Medium
**Category:** Borrow checker / collections

## Scenario
A warehouse `Inventory` backed by a `HashMap`. It can restock items that fell
below a threshold and prune items whose quantity is zero.

## Symptoms
`cargo test` fails at compile time with borrow checker errors saying a map
cannot be borrowed as mutable because it is also borrowed as immutable.

## Hints
1. Find which borrow is alive while the second one is taken. What is the loop
   holding on to?
2. Can you iterate in a way that hands you mutable access to the values
   directly, without looking the key up again?
3. For removing entries, look at what `HashMap` offers to filter in place.
4. Collecting keys first is a valid approach, but there are more idiomatic ones.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Aliasing XOR mutability, iterator borrows, `iter_mut`/`values_mut`,
`HashMap::retain`, E0502.
