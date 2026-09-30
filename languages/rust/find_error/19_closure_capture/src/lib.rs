use std::thread;

/// Builds a function that adds `n` to its input.
pub fn make_adder(n: i64) -> Box<dyn Fn(i64) -> i64> {
    Box::new(|x| x + n)
}

/// Builds a counter that grows by `step` every time it is called.
pub fn make_counter(step: u32) -> impl FnMut() -> u32 {
    let mut total = 0;
    || {
        total += step;
        total
    }
}

/// A chain of transformations applied in order.
#[derive(Default)]
pub struct Pipeline {
    stages: Vec<Box<dyn Fn(i64) -> i64>>,
}

impl Pipeline {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn add_stage(mut self, stage: Box<dyn Fn(i64) -> i64>) -> Self {
        self.stages.push(stage);
        self
    }

    pub fn run(&self, input: i64) -> i64 {
        self.stages.iter().fold(input, |acc, stage| stage(acc))
    }
}

/// Multiplies every value by `factor`, one thread per value, keeping order.
pub fn parallel_scale(values: Vec<i64>, factor: i64) -> Vec<i64> {
    let handles: Vec<_> = values
        .into_iter()
        .map(|v| thread::spawn(|| v * factor))
        .collect();
    handles.into_iter().map(|h| h.join().unwrap()).collect()
}
