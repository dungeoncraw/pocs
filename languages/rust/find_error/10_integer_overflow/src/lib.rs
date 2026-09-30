//! Numeric helpers: factorials and a simple additive checksum.

/// n! for small n.
pub fn factorial(n: u32) -> u32 {
    let mut result: u32 = 1;
    for i in 2..=n {
        result *= i;
    }
    result
}

/// Number of ways to choose `k` items out of `n` (n choose k).
pub fn choose(n: u32, k: u32) -> u32 {
    if k > n {
        return 0;
    }
    factorial(n) / (factorial(k) * factorial(n - k))
}

/// One-byte additive checksum: the sum of all bytes modulo 256.
pub fn checksum(data: &[u8]) -> u8 {
    let mut sum: u8 = 0;
    for &b in data {
        sum += b;
    }
    sum
}
