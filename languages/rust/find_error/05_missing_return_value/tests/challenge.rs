use challenge_05_missing_return_value::*;

#[test]
fn light_parcel_pays_base_rate() {
    assert_eq!(shipping_cost(800), 500);
    assert_eq!(shipping_cost(1000), 500);
}

#[test]
fn heavy_parcel_pays_extra() {
    assert_eq!(shipping_cost(1001), 575);
    assert_eq!(shipping_cost(1250), 500 + 3 * 75);
}

#[test]
fn totals_and_free_shipping() {
    assert_eq!(shipping_total(&[500, 1100]), 1075);
    assert_eq!(final_shipping(20_000, 5000), 0);
    assert_eq!(final_shipping(500, 1100), 575);
}
