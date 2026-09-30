/// Returns the cumulative sum after each price is added.
pub fn running_totals(prices: &[u32]) -> Vec<u32> {
    let mut sum = 0;
    let mut out = Vec::with_capacity(prices.len());
    for p in prices {
        sum += p;
        out.push(sum);
    }
    out
}

/// Caps a discount percentage at 100.
pub fn clamp_percent(mut percent: u32) -> u32 {
    if percent > 100 {
        percent = 100;
    }
    percent
}

pub fn apply_discount(total: u32, percent: u32) -> u32 {
    let percent = clamp_percent(percent);
    total - total * percent / 100
}

/// Total of all prices after a percentage discount.
pub fn checkout(prices: &[u32], percent: u32) -> u32 {
    let total = running_totals(prices).last().copied().unwrap_or(0);
    apply_discount(total, percent)
}
