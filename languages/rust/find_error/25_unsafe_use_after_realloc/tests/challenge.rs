use challenge_25_unsafe_use_after_realloc::*;

#[test]
fn hits_are_recorded_without_growth() {
    let mut reg = Registry::new();
    let a = reg.register("home");
    let b = reg.register("about");
    reg.hit(&a);
    reg.hit(&a);
    reg.hit(&b);
    assert_eq!(reg.hits(&a), 2);
    assert_eq!(reg.hits(&b), 1);
}

#[test]
fn hits_survive_registry_growth() {
    let mut reg = Registry::new();
    let first = reg.register("first");
    for i in 0..200 {
        reg.register(&format!("page-{i}"));
    }
    for _ in 0..10 {
        reg.hit(&first);
    }
    assert_eq!(reg.hits(&first), 10);
    assert_eq!(reg.total_hits(), 10);
}

#[test]
fn every_handle_keeps_working_after_many_registrations() {
    let mut reg = Registry::new();
    let mut handles = Vec::new();
    for i in 0..64 {
        let h = reg.register(&format!("route-{i}"));
        reg.hit(&h);
        handles.push(h);
    }
    for (i, h) in handles.iter().enumerate() {
        for _ in 0..=i {
            reg.hit(h);
        }
    }
    for (i, h) in handles.iter().enumerate() {
        assert_eq!(reg.hits(h), i as u64 + 2, "handle {i}");
        assert_eq!(reg.label(h), format!("route-{i}"));
    }
}
