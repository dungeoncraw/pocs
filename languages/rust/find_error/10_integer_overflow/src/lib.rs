//! Numeric helpers: factorials and a simple additive checksum.

/// n! for small n.
pub fn factorial(n: u64) -> u64 {
    let mut result: u64 = 1;
    for i in 2..=n {
        result = result.checked_mul(i).unwrap_or(u64::MAX);
    }
    result
}

/// Number of ways to choose `k` items out of `n` (n choose k).
pub fn choose(n: u64, k: u64) -> u64 {
    if k > n {
        return 0;
    }
    factorial(n) / (factorial(k) * factorial(n - k))
}

/// One-byte additive checksum: the sum of all bytes modulo 256.
pub fn checksum(data: &[u8]) -> u8 {
    let mut sum: u8 = 0;
    for &b in data {
        sum = sum.wrapping_add(b);
    }
    sum
}
