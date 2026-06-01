# The docs explicitly say individual members, such as a single Python class or function, cannot currently be imported directly; you import the whole module and access members through the module object.
from numpy import array


def main():
    arr = array([1, 2, 3])
    print(arr)