# Object Safety in the Shape Registry

**Difficulty:** Hard
**Category:** Traits / Compile error

## Scenario

A registry stores heterogeneous shapes as `Vec<Box<dyn Shape>>`, computes total area, scales all shapes and prints a report.

## Symptoms

`cargo test` fails to compile with error E0038 saying the trait cannot be made into an object, listing reasons for specific methods.

## Hints

1. Read the whole compiler note: it lists exactly which methods are the problem and why. `rustc --explain E0038` helps.
2. A trait object does not know the concrete type behind it. What does that imply for methods that mention `Self` in their return type, or that are generic over a type parameter?
3. The tests also call these methods on `Box<dyn Shape>` values, so "just hide them" is not enough; the methods must stay usable through a trait object.

## Goal

Make `cargo test` pass by editing only `src/lib.rs`. Do not modify the tests.

## Concepts to study

Object safety / dyn compatibility, vtables, generics vs dyn dispatch, `where Self: Sized`, returning boxed trait objects.
