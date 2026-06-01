from std.python import Python


def main() raises:
    np = Python.import_module("numpy")

    arr = np.arange(10).astype("int64")

    print("NumPy array:")
    print(arr)

    ptr = arr.ctypes.data.unsafe_get_as_pointer[DType.int64]()

    print("Reading NumPy data from Mojo pointer:")
    for i in range(10):
        print(ptr[i])