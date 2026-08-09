use std::fmt::{self, Display, Formatter};

pub struct Product {
    pub name: String,
    pub price_in_cents: u32,
}

impl Display for Product {
    fn fmt(&self, formatter: &mut Formatter<'_>) -> fmt::Result {
        write!(
            formatter,
            "{} - US$ {:.2}",
            self.name,
            self.price_in_cents as f64 / 100.0
        )
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::describe::Describe;

    #[test]
    fn product_display_formats_price_with_two_decimals() {
        let product = Product {
            name: String::from("Healing Potion"),
            price_in_cents: 1250,
        };
        assert_eq!(product.to_string(), "Healing Potion - US$ 12.50");
    }

    #[test]
    fn product_display_handles_zero_price() {
        let product = Product {
            name: String::from("Free Sample"),
            price_in_cents: 0,
        };
        assert_eq!(product.to_string(), "Free Sample - US$ 0.00");
    }

    #[test]
    fn product_describe_uses_display_impl() {
        let product = Product {
            name: String::from("Sword"),
            price_in_cents: 999,
        };
        assert_eq!(product.describe(), "Value to display: Sword - US$ 9.99");
    }
}
