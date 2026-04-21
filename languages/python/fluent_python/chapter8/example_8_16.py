from collections.abc import Sequence
from random import shuffle
from typing import TypeVar
if __name__ == '__main__':
    T = TypeVar('T')
    def sample(population: Sequence[T], size: int) -> list[T]:
        if size < 1:
            raise ValueError('size must be >= 1')
        result = list(population)
        shuffle(result)
        return result[:size]

    print(sample('ABCDEFG', 3))