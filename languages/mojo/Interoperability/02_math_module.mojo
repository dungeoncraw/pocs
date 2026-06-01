from std.python import Python


def main() raises:
    math = Python.import_module("math")

    x = math.sqrt(144)
    y = math.sin(math.pi / 2)

    print("sqrt(144):", x)
    print("sin(pi / 2):", y)