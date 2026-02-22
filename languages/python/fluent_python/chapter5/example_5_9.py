import typing

class Coordinate(typing.NamedTuple):
    lat: float
    lon: float

if __name__ == "__main__":
    trash = Coordinate('Ni!', None)
    print(trash)
