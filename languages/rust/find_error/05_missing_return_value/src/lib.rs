const BASE_RATE: u32 = 500;
const INCLUDED_GRAMS: u32 = 1000;
const RATE_PER_EXTRA_100G: u32 = 75;

/// Shipping cost in cents for a parcel of the given weight in grams.
pub fn shipping_cost(weight_grams: u32) -> u32 {
    if weight_grams <= INCLUDED_GRAMS {
        BASE_RATE
    } else {
        let extra = weight_grams - INCLUDED_GRAMS;
        let blocks = (extra + 99) / 100;
        BASE_RATE + blocks * RATE_PER_EXTRA_100G;
    }
}

/// Total cost of shipping several parcels.
pub fn shipping_total(weights: &[u32]) -> u32 {
    weights.iter().map(|w| shipping_cost(*w)).sum()
}

/// Applies free shipping over a spending threshold.
pub fn final_shipping(order_cents: u32, weight_grams: u32) -> u32 {
    if order_cents >= 10_000 {
        return 0;
    }
    shipping_cost(weight_grams)
}
