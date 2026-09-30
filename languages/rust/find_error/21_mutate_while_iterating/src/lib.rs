use std::collections::HashMap;

/// A tiny warehouse inventory keyed by item name.
#[derive(Default)]
pub struct Inventory {
    items: HashMap<String, u32>,
}

impl Inventory {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn add(&mut self, name: &str, qty: u32) {
        *self.items.entry(name.to_string()).or_insert(0) += qty;
    }

    pub fn quantity(&self, name: &str) -> u32 {
        self.items.get(name).copied().unwrap_or(0)
    }

    pub fn len(&self) -> usize {
        self.items.len()
    }

    pub fn is_empty(&self) -> bool {
        self.items.is_empty()
    }

    /// Items below `threshold` are refilled up to `target`.
    pub fn restock(&mut self, threshold: u32, target: u32) {
        for (name, qty) in self.items.iter() {
            if *qty < threshold {
                self.items.insert(name.clone(), target);
            }
        }
    }

    /// Removes items with zero quantity; returns how many were removed.
    pub fn prune_empty(&mut self) -> usize {
        let before = self.items.len();
        for (name, qty) in &self.items {
            if *qty == 0 {
                self.items.remove(name);
            }
        }
        before - self.items.len()
    }

    /// Names of items below `threshold`, sorted alphabetically.
    pub fn low_stock(&self, threshold: u32) -> Vec<String> {
        let mut names: Vec<String> = self
            .items
            .iter()
            .filter(|(_, q)| **q < threshold)
            .map(|(n, _)| n.clone())
            .collect();
        names.sort();
        names
    }
}
