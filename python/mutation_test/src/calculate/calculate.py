def calculate_tier(tier: int, total: int) -> float:
    if tier == 2:
        return total * 0.9
    return total * 1.0