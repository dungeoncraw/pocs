use std::cell::{Cell, RefCell};
use std::rc::Rc;

/// A node in a directory-like tree. Children are owned by their parent and
/// every child can navigate back up to its parent.
pub struct Node {
    name: String,
    parent: RefCell<Option<Rc<Node>>>,
    children: RefCell<Vec<Rc<Node>>>,
    drops: Rc<Cell<usize>>,
}

impl Node {
    /// `drops` is incremented every time a node is destroyed.
    pub fn new(name: &str, drops: &Rc<Cell<usize>>) -> Rc<Node> {
        Rc::new(Node {
            name: name.to_string(),
            parent: RefCell::new(None),
            children: RefCell::new(Vec::new()),
            drops: Rc::clone(drops),
        })
    }

    pub fn add_child(parent: &Rc<Node>, child: &Rc<Node>) {
        *child.parent.borrow_mut() = Some(Rc::clone(parent));
        parent.children.borrow_mut().push(Rc::clone(child));
    }

    pub fn name(&self) -> &str {
        &self.name
    }

    pub fn child_count(&self) -> usize {
        self.children.borrow().len()
    }

    pub fn parent(&self) -> Option<Rc<Node>> {
        self.parent.borrow().clone()
    }

    /// Slash-separated path from the root down to this node.
    pub fn path(&self) -> String {
        let mut parts = vec![self.name.clone()];
        let mut current = self.parent();
        while let Some(p) = current {
            parts.push(p.name.clone());
            current = p.parent();
        }
        parts.reverse();
        parts.join("/")
    }
}

impl Drop for Node {
    fn drop(&mut self) {
        self.drops.set(self.drops.get() + 1);
    }
}
