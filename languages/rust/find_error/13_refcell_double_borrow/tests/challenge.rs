use challenge_13_refcell_double_borrow::*;
use std::cell::RefCell;
use std::rc::Rc;

#[test]
fn publish_delivers_to_all_subscribers() {
    let bus = EventBus::new();
    let log = Rc::new(RefCell::new(Vec::<String>::new()));
    for name in ["a", "b"] {
        let log = Rc::clone(&log);
        bus.subscribe(move |_, ev| log.borrow_mut().push(format!("{name}:{ev}")));
    }
    bus.publish("hello");
    assert_eq!(*log.borrow(), vec!["a:hello", "b:hello"]);
    assert_eq!(bus.delivered(), 2);
}

#[test]
fn handler_can_publish_follow_up_event() {
    let bus = EventBus::new();
    let log = Rc::new(RefCell::new(Vec::<String>::new()));
    bus.subscribe(|bus, ev| {
        if ev == "start" {
            bus.publish("follow-up");
        }
    });
    let l = Rc::clone(&log);
    bus.subscribe(move |_, ev| l.borrow_mut().push(ev.to_string()));
    bus.publish("start");
    assert_eq!(*log.borrow(), vec!["follow-up", "start"]);
}

#[test]
fn handler_can_subscribe_while_event_is_being_published() {
    let bus = EventBus::new();
    let log = Rc::new(RefCell::new(Vec::<String>::new()));
    let l1 = Rc::clone(&log);
    bus.subscribe(move |bus, ev| {
        l1.borrow_mut().push(format!("first:{ev}"));
        if ev == "init" {
            let l2 = Rc::clone(&l1);
            bus.subscribe(move |_, ev| l2.borrow_mut().push(format!("late:{ev}")));
        }
    });
    bus.publish("init");
    assert_eq!(bus.handler_count(), 2);
    bus.publish("tick");
    assert_eq!(*log.borrow(), vec!["first:init", "first:tick", "late:tick"]);
}
