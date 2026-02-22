from typing import NamedTuple

class Coordinate(NamedTuple):
    lat: float
    lon: float
    def __str__(self):
        ns = 'N' if self.lat >= 0 else 'S'
        we = 'E' if self.lon >= 0 else 'W'
        return f'{abs(self.lat):.1f}°{ns}, {abs(self.lon):.1f}°{we}'

if __name__ == "__main__":
    cord = Coordinate(55.7558, 37.6173)
    print(cord)
    print(isinstance(cord, Coordinate))
    print(issubclass(Coordinate,cord.__class__))
    print(issubclass(Coordinate, NamedTuple.__class__))
    print(issubclass(Coordinate, tuple))