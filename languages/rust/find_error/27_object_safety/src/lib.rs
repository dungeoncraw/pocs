use std::f64::consts::PI;
use std::fmt::Write;

pub trait Shape {
    fn name(&self) -> String;
    fn area(&self) -> f64;
    fn scaled(&self, factor: f64) -> Self;
    fn describe<W: Write>(&self, out: &mut W) -> std::fmt::Result {
        write!(out, "{} (area {:.2})", self.name(), self.area())
    }
}

pub struct Circle {
    pub radius: f64,
}

pub struct Rect {
    pub w: f64,
    pub h: f64,
}

impl Shape for Circle {
    fn name(&self) -> String {
        "circle".to_string()
    }
    fn area(&self) -> f64 {
        PI * self.radius * self.radius
    }
    fn scaled(&self, factor: f64) -> Self {
        Circle { radius: self.radius * factor }
    }
}

impl Shape for Rect {
    fn name(&self) -> String {
        "rect".to_string()
    }
    fn area(&self) -> f64 {
        self.w * self.h
    }
    fn scaled(&self, factor: f64) -> Self {
        Rect { w: self.w * factor, h: self.h * factor }
    }
}

#[derive(Default)]
pub struct ShapeRegistry {
    shapes: Vec<Box<dyn Shape>>,
}

impl ShapeRegistry {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn add(&mut self, shape: Box<dyn Shape>) {
        self.shapes.push(shape);
    }

    pub fn len(&self) -> usize {
        self.shapes.len()
    }

    pub fn is_empty(&self) -> bool {
        self.shapes.is_empty()
    }

    pub fn total_area(&self) -> f64 {
        self.shapes.iter().map(|s| s.area()).sum()
    }

    pub fn scale_all(&self, factor: f64) -> ShapeRegistry {
        ShapeRegistry { shapes: self.shapes.iter().map(|s| s.scaled(factor)).collect() }
    }

    pub fn report(&self) -> String {
        let mut out = String::new();
        for s in &self.shapes {
            s.describe(&mut out).unwrap();
            out.push('\n');
        }
        out
    }
}
