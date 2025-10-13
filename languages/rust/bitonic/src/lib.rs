#![doc = include_str!(concat!(env!("CARGO_MANIFEST_DIR"), "/README.md"))]
#![deny(rustdoc::broken_intra_doc_links)]
#![cfg_attr(docsrs, feature(doc_cfg))]
pub fn build_bitonic(n: usize, l: i64, r: i64) -> Result<Vec<i64>, ()> {
    if l > r {
        return Err(());
    }
    let m = r - l + 1;
    if m <= 0 {
        return Err(());
    }
    if (n as i64) > 2 * m - 1 {
        return Err(());
    }

    let mut right_including_peak = (n as i64).min(m) as usize;
    let mut left_len = n - right_including_peak;

    if n >= 2 && left_len == 0 {
        left_len = 1;
        right_including_peak = n - 1;
    }

    let mut res = Vec::with_capacity(n);
    if left_len > 0 {
        let start = r - left_len as i64;
        for x in start..=r - 1 {
            res.push(x);
        }
    }

    for i in 0..right_including_peak {
        res.push(r - i as i64);
    }

    debug_assert_eq!(res.len(), n);
    Ok(res)
}

#[cfg(test)]
mod tests {
    use super::build_bitonic;
    use pretty_assertions::assert_eq;

    #[test]
    fn examples() {
        assert_eq!(build_bitonic(5, 3, 10).unwrap(), vec![9, 10, 9, 8, 7]);
        assert_eq!(build_bitonic(7, 2, 5).unwrap(),  vec![2, 3, 4, 5, 4, 3, 2]);
    }

    #[test]
    fn single_element() {
        assert_eq!(build_bitonic(1, 5, 5).unwrap(), vec![5]);
        assert_eq!(build_bitonic(1, 3, 10).unwrap(), vec![10]); // peak only
    }

    #[test]
    fn impossible_cases() {
        assert!(build_bitonic(9, 1, 3).is_err()); // 2*m-1 = 5 < 9
        assert!(build_bitonic(2, 3, 3).is_err()); // only one distinct value => can't be strictly bitonic with n=2
        assert!(build_bitonic(4, 10, 8).is_err()); // l > r
    }

    #[test]
    fn boundaries() {
        // Just at the max allowed length (2*m-1)
        // l=1, r=3 => m=3 => max length 5
        assert!(build_bitonic(5, 1, 3).is_ok());
        assert!(build_bitonic(6, 1, 3).is_err());
    }
}