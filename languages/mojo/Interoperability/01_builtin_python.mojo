from std.python import Python


def main() raises:
    builtins = Python.import_module("builtins")

    value = builtins.abs(-42)
    print("abs(-42) from Python:", value)

    text = builtins.str(12345)
    print("str(12345) from Python:", text)

    py_type = builtins.type(value)
    print("Python type:", py_type)