use std::fmt::Display;

pub trait Describe {
    fn describe(&self) -> String;
}

impl<T> Describe for T
where
    T: Display + ?Sized,
{
    fn describe(&self) -> String {
        format!("Value to display: {}", self)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn describe_formats_integer() {
        assert_eq!(42.describe(), "Value to display: 42");
    }

    #[test]
    fn describe_formats_str_slice() {
        assert_eq!("Rust".describe(), "Value to display: Rust");
    }

    #[test]
    fn describe_formats_owned_string() {
        let owned = String::from("Bridgeway");
        assert_eq!(owned.describe(), "Value to display: Bridgeway");
    }

    #[test]
    fn describe_formats_bool() {
        assert_eq!(true.describe(), "Value to display: true");
        assert_eq!(false.describe(), "Value to display: false");
    }
}
