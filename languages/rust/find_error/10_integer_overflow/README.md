# Integer Overflow

- **Difficulty:** Medium
- **Category:** Runtime panic / arithmetic

## Scenario

Numeric helpers: factorials, binomial coefficients and a one-byte additive checksum (sum of bytes modulo 256).

## Symptoms

In debug builds (the default for `cargo test`) some tests panic with `attempt to multiply with overflow` or `attempt to add with overflow`.

## Hints

1. The panic message names the operation; find which integer type is too small for the values involved.
2. Overflow panics only happen in debug builds; in release mode the value silently wraps. Neither is always what you want.
3. Decide per function: does the result need more range, or is wrapping the intended behavior? Check the integer methods `wrapping_*`, `checked_*`.

## Goal

Make `cargo test` pass. Only edit `src/lib.rs`; do not modify the tests.

## Concepts to study

- Integer types and ranges
- Overflow behavior in debug vs release
- `wrapping_add`, `checked_mul`, `saturating_*`
