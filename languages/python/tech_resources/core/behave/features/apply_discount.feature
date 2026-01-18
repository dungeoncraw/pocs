Feature: Discount System
  Scenario: Apply a 10% discount coupon
    Given the purchase price is 100
    And the coupon code is "SAVE10"
    When I calculate the final price
    Then the total price should be 90

  Scenario: Apply an invalid discount coupon
    Given the purchase price is 100
    And the coupon code is "INVALID"
    When I calculate the final price
    Then the total price should be 100