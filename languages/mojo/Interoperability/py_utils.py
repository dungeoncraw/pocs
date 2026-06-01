import random


def add(a, b):
    return a + b


def greeting(name):
    return f"Hello, {name}, from Python!"


def random_scores(n):
    return [random.random() for _ in range(n)]


class Greeter:
    def __init__(self, name):
        self.name = name

    def hello(self):
        return f"Hello from Python class, {self.name}!"