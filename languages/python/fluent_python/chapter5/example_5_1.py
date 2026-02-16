class Coordinates:
    def __init__(self, lat, lon):
        self.lat = lat
        self.lon = lon

if __name__ == "__main__":
    moscow = Coordinates(55.7558, 37.6173)
    print(f'Moscow coordinates: lat={moscow.lat}, lon={moscow.lon}')
    location = Coordinates(55.7558, 37.6173)
    print(location == moscow)
    print((location.lat, location.lon) == (moscow.lat, moscow.lon))