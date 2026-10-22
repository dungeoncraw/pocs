use std::cell::{Cell, RefCell};

#[derive(Debug, PartialEq, Clone)]
pub struct Record {
    pub key: String,
    pub value: i32,
}

fn parse(line: &str) -> Option<Record> {
    let (key, value) = line.split_once('=')?;
    let value = value.trim().parse().ok()?;
    Some(Record {
        key: key.trim().to_string(),
        value,
    })
}

/// Imports `key=value` lines. Well-formed lines become records, malformed
/// lines are remembered so they can be reported later.
#[derive(Default)]
pub struct Importer {
    seen: Cell<usize>,
    rejected: RefCell<Vec<String>>,
}

impl Importer {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn import(&self, lines: &[&str]) -> Vec<Record> {
        lines
            .iter()
            .filter_map(|&line| {
                self.seen.set(self.seen.get() + 1);
                match parse(line) {
                    Some(record) => Some(record),
                    None => {
                        self.rejected.borrow_mut().push(line.to_string());
                        None
                    }
                }
            })
            .collect()
    }

    /// Number of lines examined so far.
    pub fn seen(&self) -> usize {
        self.seen.get()
    }

    pub fn rejected(&self) -> Vec<String> {
        self.rejected.borrow().clone()
    }
}
