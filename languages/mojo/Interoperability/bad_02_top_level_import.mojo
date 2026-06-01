from std.python import Python


# This does NOT work.
#The docs say Python.import_module() must be inside another method/function, so you may need to import the module inside each function that needs it or pass the module object around
np = Python.import_module("numpy")


def main() raises:
    arr = np.array(Python.list(1, 2, 3))
    print(arr)