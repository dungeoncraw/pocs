use challenge_27_object_safety::*;

fn sample() -> ShapeRegistry {
    let mut reg = ShapeRegistry::new();
    reg.add(Box::new(Circle { radius: 1.0 }));
    reg.add(Box::new(Rect { w: 2.0, h: 3.0 }));
    reg
}

#[test]
fn total_area_sums_all_shapes() {
    let reg = sample();
    assert_eq!(reg.len(), 2);
    assert!((reg.total_area() - (std::f64::consts::PI + 6.0)).abs() < 1e-9);
}

#[test]
fn scaling_multiplies_area_by_factor_squared() {
    let reg = sample();
    let big = reg.scale_all(2.0);
    assert_eq!(big.len(), 2);
    assert!((big.total_area() - 4.0 * reg.total_area()).abs() < 1e-9);
}

#[test]
fn report_lists_each_shape() {
    let report = sample().report();
    assert_eq!(report, "circle (area 3.14)\nrect (area 6.00)\n");
}

#[test]
fn shapes_usable_as_trait_objects_directly() {
    let shapes: Vec<Box<dyn Shape>> = vec![Box::new(Rect { w: 1.0, h: 1.0 }), Box::new(Circle { radius: 2.0 })];
    let names: Vec<String> = shapes.iter().map(|s| s.name()).collect();
    assert_eq!(names, ["rect", "circle"]);
    let doubled: Vec<Box<dyn Shape>> = shapes.iter().map(|s| s.scaled(2.0)).collect();
    assert!((doubled[0].area() - 4.0).abs() < 1e-9);
    let mut out = String::new();
    doubled[0].describe(&mut out).unwrap();
    assert_eq!(out, "rect (area 4.00)");
}
