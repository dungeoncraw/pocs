from std.python import Python


def main() raises:
    py_list = Python.list("Mojo", 123, 3.14)
    print("Python list:", py_list)

    py_list.append("new item")
    print("After append:", py_list)

    py_dict = Python.dict()
    py_dict["language"] = "Mojo"
    py_dict["interop"] = "Python"
    py_dict["works"] = True

    print("Python dict:", py_dict)

    py_tuple = Python.tuple("AI", "SIMD", "Python interop")
    print("Python tuple:", py_tuple)
    print("Tuple item 1:", py_tuple[1])