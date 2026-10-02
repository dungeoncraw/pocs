//! Sliding-window statistics over sensor readings.

/// Simple moving average. For `data` of length n and window w, returns
/// n - w + 1 averages. Returns an empty vector if `window` is 0 or
/// larger than the data.
pub fn moving_average(data: &[f64], window: usize) -> Vec<f64> {
    if window == 0 || window > data.len() {
        return Vec::new();
    }
    let count = data.len() - window + 1;
    let mut out = Vec::with_capacity(count);
    for start in 0..count {
        let mut sum = 0.0;
        for i in start..start + window {
            sum += data[i];
        }
        out.push(sum / window as f64);
    }
    out
}

/// Maximum of each window of `window` consecutive readings.
pub fn window_max(data: &[i32], window: usize) -> Vec<i32> {
    if window == 0 || window > data.len() {
        return Vec::new();
    }
    data.windows(window)
        .map(|w| *w.iter().max().unwrap())
        .collect()
}
