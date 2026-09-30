use challenge_14_rc_cycle_leak::*;
use std::cell::Cell;
use std::rc::Rc;

fn counter() -> Rc<Cell<usize>> {
    Rc::new(Cell::new(0))
}

#[test]
fn path_walks_up_to_the_root() {
    let drops = counter();
    let root = Node::new("root", &drops);
    let usr = Node::new("usr", &drops);
    let bin = Node::new("bin", &drops);
    Node::add_child(&root, &usr);
    Node::add_child(&usr, &bin);
    assert_eq!(bin.path(), "root/usr/bin");
    assert_eq!(root.child_count(), 1);
    assert_eq!(bin.parent().unwrap().name(), "usr");
}

#[test]
fn children_do_not_keep_their_parent_alive() {
    let drops = counter();
    let root = Node::new("root", &drops);
    for name in ["a", "b"] {
        let c = Node::new(name, &drops);
        Node::add_child(&root, &c);
    }
    assert_eq!(root.child_count(), 2);
    assert_eq!(Rc::strong_count(&root), 1);
}

#[test]
fn dropping_the_root_destroys_the_whole_tree() {
    let drops = counter();
    {
        let root = Node::new("root", &drops);
        let usr = Node::new("usr", &drops);
        let bin = Node::new("bin", &drops);
        Node::add_child(&root, &usr);
        Node::add_child(&usr, &bin);
        assert_eq!(drops.get(), 0);
    }
    assert_eq!(drops.get(), 3, "every node should have been dropped");
}

#[test]
fn orphaned_child_reports_no_parent() {
    let drops = counter();
    let child = Node::new("child", &drops);
    {
        let root = Node::new("root", &drops);
        Node::add_child(&root, &child);
        assert!(child.parent().is_some());
    }
    assert_eq!(drops.get(), 1, "only the root should be gone");
    assert!(child.parent().is_none());
    assert_eq!(child.path(), "child");
}
