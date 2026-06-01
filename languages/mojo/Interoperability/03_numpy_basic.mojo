from std.python import Python


def main() raises:
    np = Python.import_module("numpy")

    arr = np.array(Python.list(1.0, 2.0, 3.0, 4.0, 5.0))

    print("NumPy array:")
    print(arr)

    print("Shape:")
    print(arr.shape)

    print("Sum:")
    print(arr.sum())

    print("Mean:")
    print(arr.mean())