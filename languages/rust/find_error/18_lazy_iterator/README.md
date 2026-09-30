# 18 - Lazy iterator

- **Difficulty:** Medium
- **Category:** Iterators / logic bug

## Scenario
`Importer` parses `key=value` lines. Good lines become `Record`s, bad lines must be skipped and remembered in a rejected list, and the importer keeps a count of lines it has examined.

## Symptoms
The code compiles (possibly with warnings) and nothing panics, but tests fail on assertions: records after the first bad line are missing, and the list of rejected lines is empty even though bad lines were present.

## Hints
1. Read the compiler warnings carefully; `cargo clippy` may say more.
2. Iterator adaptors such as `map` do nothing until something drives them. What drives them here?
3. Compare the semantics of `take_while` and `filter` on a sequence with a "bad" element in the middle.
4. Use `dbg!` or a print inside the closures to see which ones actually run.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
Lazy evaluation of iterators, `#[must_use]` on adaptors, consuming methods (`for_each`, `collect`, `count`), `take_while` vs `filter`, side effects in iterator closures.
