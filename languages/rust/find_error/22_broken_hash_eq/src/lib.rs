use std::collections::HashSet;
use std::hash::{Hash, Hasher};

/// A social-media handle. Handles are case-insensitive: "Rusty" and "rusty"
/// refer to the same account.
#[derive(Debug, Clone)]
pub struct Handle(String);

impl Handle {
    pub fn new(name: &str) -> Self {
        Handle(name.to_string())
    }

    pub fn as_str(&self) -> &str {
        &self.0
    }
}

impl PartialEq for Handle {
    fn eq(&self, other: &Self) -> bool {
        self.0.eq_ignore_ascii_case(&other.0)
    }
}

impl Eq for Handle {}

impl Hash for Handle {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.0.hash(state);
    }
}

/// Removes duplicate handles, keeping the first spelling seen and the order.
pub fn dedup_handles(raw: &[&str]) -> Vec<Handle> {
    let mut seen = HashSet::new();
    let mut out = Vec::new();
    for name in raw {
        let handle = Handle::new(name);
        if seen.insert(handle.clone()) {
            out.push(handle);
        }
    }
    out
}
