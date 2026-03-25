def factorial(n):
    """returns n!"""
    return 1 if n < 2 else n * factorial(n - 1)

if __name__ == "__main__":
    print(factorial(5))
    print(factorial.__doc__)

    fact = factorial
    print(fact(5))
    print(fact)

    print(list(map(factorial, range(6))))
    print([fact(n) for n in range(6)])
    print(list(map(factorial, filter(lambda n: n % 2, range(6)))))
    print([factorial(n) for n in range(6) if n % 2])