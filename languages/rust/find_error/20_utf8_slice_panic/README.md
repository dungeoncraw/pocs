# 20 - UTF-8 Slice Panic

**Difficulty:** Medium
**Category:** Strings / runtime panic

## Scenario
Text helpers for a UI: `truncate` shortens text to N characters with an
ellipsis, and `title_case` capitalizes each word of a sentence.

## Symptoms
ASCII tests pass, but tests with accents, emoji or CJK text either panic with a
message about a byte index and a character boundary, or produce output that is
shorter/longer than expected.

## Hints
1. Run `RUST_BACKTRACE=1 cargo test` and read the panic message and location.
2. What unit does `str::len()` return? What unit does slicing with `[a..b]` use?
3. A `char` is not always one byte. Look at the `chars()` and `char_indices()` APIs.
4. There may be more than one place with the same kind of mistake.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
UTF-8 encoding, `str` byte indexing vs `char`s, `char_indices`, `chars().take(n)`,
char boundaries.
