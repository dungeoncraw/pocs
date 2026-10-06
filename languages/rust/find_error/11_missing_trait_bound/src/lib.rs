//! Generic helpers over slices.

use std::fmt::Display;

/// Returns the largest item, or `None` for an empty slice.
pub fn largest<T: PartialOrd + Copy>(items: &[T]) -> Option<T> {
    let mut best = *items.first()?;
    for &item in items {
        if item > best {
            best = item;
        }
    }
    Some(best)
}

/// Joins the textual form of each item with ", ".
pub fn describe_all<T: Display>(items: &[T]) -> String {
    items
        .iter()
        .map(|item| item.to_string())
        .collect::<Vec<_>>()
        .join(", ")
}

/// Text like "largest of [1, 2, 3] is 3".
pub fn report<T>(items: &[T]) -> String
where
    T: PartialOrd + Copy + Display
{
    match largest(items) {
        Some(max) => format!("largest of [{}] is {}", describe_all(items), max),
        None => "no items".to_string(),
    }
}
