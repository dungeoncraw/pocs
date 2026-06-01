from std.python import Python


def main() raises:
    # Add current directory to Python import path.
    Python.add_to_path(".")

    py_utils = Python.import_module("py_utils")

    print("add(10, 20):", py_utils.add(10, 20))
    print(py_utils.greeting("Thiago"))

    scores = py_utils.random_scores(5)
    print("Random scores from Python:", scores)

    greeter = py_utils.Greeter("Mojo")
    print(greeter.hello())