use challenge_10_integer_overflow::*;

#[test]
fn small_factorials() {
    assert_eq!(factorial(0), 1);
    assert_eq!(factorial(5), 120);
    assert_eq!(u64::from(factorial(10)), 3_628_800);
}

#[test]
fn large_factorial() {
    assert_eq!(u64::from(factorial(20)), 2_432_902_008_176_640_000u64);
}

#[test]
fn choose_small() {
    assert_eq!(choose(5, 2), 10);
    assert_eq!(choose(3, 5), 0);
}

#[test]
fn choose_needs_big_intermediate() {
    assert_eq!(choose(20, 10), 184_756);
}

#[test]
fn checksum_small() {
    assert_eq!(checksum(&[1, 2, 3]), 6);
    assert_eq!(checksum(&[]), 0);
}

#[test]
fn checksum_wraps_modulo_256() {
    assert_eq!(checksum(&[200, 100, 50]), 94);
    assert_eq!(checksum(&[255, 1]), 0);
}
