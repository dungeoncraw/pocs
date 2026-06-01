from std.python import Python


def main() raises:
    result = Python.evaluate("10 + 20 + 30")
    print("Python evaluated result:", result)

    py_set = Python.evaluate("{2, 3, 2, 7, 11, 3}")
    print("Python set:", py_set)
    print("Set length:", len(py_set))

    contained = 7 in py_set
    print("Does set contain 7?", contained)