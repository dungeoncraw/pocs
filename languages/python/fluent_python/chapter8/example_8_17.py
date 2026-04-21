from collections import Counter
from collections.abc import Iterable
def mode(data: Iterable[float]) -> float:
    pairs = Counter(data).most_common(1)
    if len(pairs) == 0:
        raise ValueError('no mode for empty data')
    return pairs[0][0]

if __name__ == '__main__':
    print(mode([1, 2, 3, 2, 1, 2, 3, 4, 2, 2, 3]))