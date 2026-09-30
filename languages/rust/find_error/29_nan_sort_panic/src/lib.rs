//! Leaderboard statistics over raw sensor/score readings.
//!
//! Readings that are NaN are invalid (sensor dropouts) and must be ignored by
//! every statistic and by the ranking.

#[derive(Debug, Clone, PartialEq)]
pub struct Entry {
    pub name: String,
    pub score: f64,
}

pub fn entry(name: &str, score: f64) -> Entry {
    Entry { name: name.to_string(), score }
}

/// Entries ordered from highest to lowest score.
pub fn ranking(entries: &[Entry]) -> Vec<Entry> {
    let mut sorted = entries.to_vec();
    sorted.sort_by(|a, b| b.score.partial_cmp(&a.score).unwrap());
    sorted
}

pub fn top_n(entries: &[Entry], n: usize) -> Vec<String> {
    ranking(entries).into_iter().take(n).map(|e| e.name).collect()
}

pub fn max_score(entries: &[Entry]) -> Option<f64> {
    entries
        .iter()
        .map(|e| e.score)
        .max_by(|a, b| a.partial_cmp(b).unwrap())
}

pub fn mean(values: &[f64]) -> Option<f64> {
    if values.is_empty() {
        return None;
    }
    Some(values.iter().sum::<f64>() / values.len() as f64)
}

pub fn median(values: &[f64]) -> Option<f64> {
    if values.is_empty() {
        return None;
    }
    let mut v = values.to_vec();
    v.sort_by(|a, b| a.partial_cmp(b).unwrap());
    let mid = v.len() / 2;
    Some(if v.len() % 2 == 0 { (v[mid - 1] + v[mid]) / 2.0 } else { v[mid] })
}
