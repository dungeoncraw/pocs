# 22 - Broken Hash / Eq

**Difficulty:** Medium
**Category:** Traits / logic bug

## Scenario
`Handle` models case-insensitive account names ("Rusty" == "rusty") and is used
as a key in `HashSet`/`HashMap` to deduplicate and look up accounts.

## Symptoms
The code compiles and no test panics on its own, but lookups in sets and maps
silently miss entries, and deduplication keeps duplicates. Tests that compare
handles directly with `==` pass.

## Hints
1. `==` works, but collections still misbehave. What else do hash-based
   collections rely on besides equality?
2. Read the documentation of the `Hash` trait: what relationship must hold
   between `Hash` and `Eq`?
3. Write a tiny experiment (or use `dbg!`) that prints hashes of two handles
   that compare equal.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
The `Hash`/`Eq` contract (`k1 == k2` implies `hash(k1) == hash(k2)`),
`HashMap` buckets, manual trait implementations, case folding.
