from std.python import Python
from std.python import PythonObject


def main() raises:
    py_int = PythonObject(123)
    py_float = PythonObject(3.14159)
    py_bool = PythonObject(True)
    py_string = PythonObject("Hello from PythonObject")

    mojo_int = Int(py=py_int)
    mojo_float = Float64(py=py_float)
    mojo_bool = Bool(py=py_bool)
    mojo_string = String(py=py_string)

    print("Mojo Int:", mojo_int + 10)
    print("Mojo Float64:", mojo_float * 2.0)
    print("Mojo Bool:", mojo_bool)
    print("Mojo String:", mojo_string)