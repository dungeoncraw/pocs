from behave import given, when, then
from main import calculate_discount

@given('the purchase price is {price:d}')
def step_impl(context, price):
    context.initial_price = price

@given('the coupon code is "{coupon}"')
def step_impl(context, coupon):
    context.coupon = coupon

@when('I calculate the final price')
def step_impl(context):
    context.result = calculate_discount(context.initial_price, context.coupon)

@then('the total price should be {expected_price:g}')
def step_impl(context, expected_price):
    assert float(context.result) == float(expected_price), \
        f"Expected {expected_price}, but got {context.result}"