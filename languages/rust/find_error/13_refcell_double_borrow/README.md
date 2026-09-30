# 13 - RefCell double borrow

- **Difficulty:** Medium
- **Category:** Interior mutability / runtime borrow checking

## Scenario
`EventBus` is a small single-threaded publish/subscribe bus. Handlers get a reference to the bus so they can publish follow-up events or register new handlers in response to an event. A handler registered during a publish should only see later events.

## Symptoms
`cargo test` compiles fine, but at least one test panics at runtime with a message about a `RefCell` being already borrowed (`BorrowMutError`). Other tests pass.

## Hints
1. The compiler is happy; this is a *runtime* check. Read the panic message and the location it points to.
2. Run `RUST_BACKTRACE=1 cargo test` and find which two borrows of the same `RefCell` overlap in time.
3. Ask yourself: how long does a `Ref` guard live, and what does user-supplied code do in that window?
4. Consider whether you really need to hold the guard while calling out to arbitrary code.

## Goal
`cargo test` passes. Do not modify the tests; edit only `src/lib.rs`.

## Concepts to study
`RefCell::borrow` / `borrow_mut`, guard lifetimes, re-entrancy, `Rc` cloning to snapshot data.
