from functools import reduce

if __name__ == '__main__':
    big_numbers = range(10_000_000)

    # map the function over the iterable, without processing it as it is an iterator
    mapped_func = map(lambda x: x ** 2, filter(lambda x: x % 2 == 0, big_numbers))

    print(f"Map object {mapped_func}")  # <map object at 0x...>
    print(f"Nex item of iterator: {next(mapped_func)}")  # 0
    print(f"Nex item of iterator: {next(mapped_func)}")  # 4
    print(f"Nex item of iterator: {next(mapped_func)}")  # 16

    numbers = range(10)
    print(f"Sum from 0 to 10: {reduce(lambda x, y: x + y, numbers)}")
    # more pythonic way sum(numbers)

    names = ['John', 'Jane', 'Bob', 'Ana', 'Gray']
    # filter names that start with J
    print(f"Names that starts with J: {list(filter(lambda x: x.startswith('J'), names))}")

