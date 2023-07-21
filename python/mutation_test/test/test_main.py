from src.calculate import calculate

def test_tier_1_discount():
    assert calculate.calculate_tier(1, 100) == 100.0

def test_tier_2_discount():
    assert calculate.calculate_tier(2, 100) == 90.0

def test_tier_gte_tier():
    assert calculate.calculate_tier(3, 100) == 100.0
