# Unsafe: The Stale Handle

**Difficulty:** Hard
**Category:** Unsafe / memory safety

## Scenario

A hit-counter `Registry` hands out `Handle`s so callers can record hits on a label quickly. Totals are read back through the registry's slot table.

## Symptoms

`cargo test` on the growth tests misbehaves badly: the process may abort with a memory or alignment related message, crash with a signal, or (if you are lucky) report hits that went missing. The small test without growth passes.

## Hints

1. Everything works with a few labels and breaks with many. What changes inside the registry as it gets bigger?
2. Ask yourself what a raw pointer into a `Vec` element still points to after the Vec has moved its buffer. Run `cargo +nightly miri test` if available, or RUST_BACKTRACE=1, to see the invalid access.
3. Is the `unsafe` block needed at all here? Think about what stable identity a handle can hold.

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

Raw pointers, Vec reallocation and pointer invalidation, aliasing, undefined behavior, Miri, indices vs pointers.
