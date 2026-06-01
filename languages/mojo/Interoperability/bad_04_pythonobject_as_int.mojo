from std.python import Python


def main() raises:
    value = Python.evaluate("123")

    # This is wrong because value is a PythonObject, not a native Mojo Int.
    var native_int: Int = value

    print(native_int)


"""
correct version

from std.python import Python


def main() raises:
    value = Python.evaluate("123")
    var native_int = Int(py=value)

    print(native_int + 10)
"""