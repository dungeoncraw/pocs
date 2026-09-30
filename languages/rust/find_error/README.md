# Rust Debugging Challenges

30 small crates, each with a bug. Your job: make `cargo test` pass by editing **only `src/lib.rs`**. Never edit `tests/`.

```sh
cd 01_immutable_assign
cargo test
```

Read the folder's `README.md` for the scenario and hints (no solutions). Useful tools: `RUST_BACKTRACE=1`, `cargo clippy`, `dbg!`, `cargo +nightly miri test`.

| # | Challenge | Level | Kind |
|---|-----------|-------|------|
| 01 | immutable_assign | Easy | compile |
| 02 | type_mismatch | Easy | compile |
| 03 | use_after_move | Easy | compile |
| 04 | borrow_conflict | Easy | compile |
| 05 | missing_return_value | Easy | compile |
| 06 | non_exhaustive_match | Easy | compile |
| 07 | missing_lifetime | Easy | compile |
| 08 | unwrap_none_panic | Easy | runtime panic |
| 09 | index_out_of_bounds | Easy | runtime panic |
| 10 | integer_overflow | Medium | runtime panic |
| 11 | missing_trait_bound | Easy | compile |
| 12 | question_mark_in_wrong_fn | Medium | compile |
| 13 | refcell_double_borrow | Medium | runtime panic |
| 14 | rc_cycle_leak | Medium | memory / logic |
| 15 | rc_not_send | Medium | compile |
| 16 | mutex_deadlock | Medium | hang (test times out) |
| 17 | return_ref_to_local | Medium | compile |
| 18 | lazy_iterator | Medium | logic |
| 19 | closure_capture | Medium | compile |
| 20 | utf8_slice_panic | Medium | runtime panic |
| 21 | mutate_while_iterating | Medium | compile |
| 22 | broken_hash_eq | Medium | logic |
| 23 | channel_never_closes | Medium | hang (test times out) |
| 24 | stack_overflow | Medium | crash |
| 25 | unsafe_use_after_realloc | Hard | UB / crash |
| 26 | lost_update_race | Hard | concurrency |
| 27 | object_safety | Hard | compile |
| 28 | drop_order_guard | Hard | logic |
| 29 | nan_sort_panic | Hard | runtime panic |
| 30 | quadratic_perf | Hard | performance |
