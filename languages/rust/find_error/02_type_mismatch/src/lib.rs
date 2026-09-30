/// Formats a money amount in cents as dollars, e.g. 1999 -> "$19.99".
pub fn format_money(cents: u64) -> String {
    format!("${}.{:02}", cents / 100, cents % 100)
}

/// Title-cases the first letter of an item name.
pub fn display_name(name: &str) -> String {
    let mut chars = name.chars();
    match chars.next() {
        Some(first) => first.to_uppercase().collect::<String>() + chars.as_str(),
        None => name.to_string(),
    }
}

/// Builds one invoice line: "Widget x3 = $30.00".
pub fn invoice_line(name: &str, qty: u64, unit_cents: u64) -> String {
    let total = qty * unit_cents;
    format!("{} x{} = {}", display_name(name), qty, format_money(total))
}

/// Sum of the totals of all lines, in cents.
pub fn invoice_total(lines: &[(u32, u32)]) -> u32 {
    lines.iter().map(|(q, c)| q * c).sum()
}
