use challenge_19_closure_capture::*;

#[test]
fn adder_adds_captured_value() {
    let add5 = make_adder(5);
    assert_eq!(add5(10), 15);
    assert_eq!(add5(-5), 0);
}

#[test]
fn counter_keeps_its_own_state() {
    let mut c = make_counter(3);
    assert_eq!(c(), 3);
    assert_eq!(c(), 6);
    assert_eq!(c(), 9);
    let mut other = make_counter(10);
    assert_eq!(other(), 10);
    assert_eq!(c(), 12);
}

#[test]
fn pipeline_applies_stages_in_order() {
    let p = Pipeline::new()
        .add_stage(make_adder(2))
        .add_stage(Box::new(|x| x * 10))
        .add_stage(make_adder(-1));
    assert_eq!(p.run(1), 29);
    assert_eq!(p.run(0), 19);
}

#[test]
fn parallel_scale_keeps_order() {
    assert_eq!(parallel_scale(vec![1, 2, 3, 4], 7), vec![7, 14, 21, 28]);
    assert!(parallel_scale(vec![], 3).is_empty());
}
