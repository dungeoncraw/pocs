from functools import reduce
from operator import mul

def factorial(n):
    return reduce(lambda a, b: a*b, range(1, n+1))

def factorial_mul(n):
    return reduce(mul, range(1, n+1))