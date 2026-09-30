/// A singly linked list of numbers.
pub struct List {
    head: Option<Box<Node>>,
}

struct Node {
    value: u64,
    next: Option<Box<Node>>,
}

impl List {
    pub fn new() -> Self {
        List { head: None }
    }

    pub fn push_front(&mut self, value: u64) {
        let next = self.head.take();
        self.head = Some(Box::new(Node { value, next }));
    }

    /// Builds the list 1, 2, ..., n.
    pub fn counting(n: u64) -> Self {
        let mut list = List::new();
        for v in (1..=n).rev() {
            list.push_front(v);
        }
        list
    }

    pub fn len(&self) -> usize {
        fn go(node: &Option<Box<Node>>) -> usize {
            match node {
                None => 0,
                Some(n) => 1 + go(&n.next),
            }
        }
        go(&self.head)
    }

    pub fn is_empty(&self) -> bool {
        self.head.is_none()
    }

    pub fn sum(&self) -> u64 {
        fn go(node: &Option<Box<Node>>) -> u64 {
            match node {
                None => 0,
                Some(n) => n.value + go(&n.next),
            }
        }
        go(&self.head)
    }
}

impl Default for List {
    fn default() -> Self {
        Self::new()
    }
}
