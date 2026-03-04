import typing
class City(typing.NamedTuple):
    continent: str
    name: str
    country: str

if __name__ == "__main__":
    cities = [
        City('Asia', 'Tokyo', 'JP'),
        City('Asia', 'Delhi', 'IN'),
        City('North America', 'Mexico City', 'MX'),
        City('North America', 'New York', 'US'),
        City('South America', 'São Paulo', 'BR'),
    ]

    def match_asian_cities():
        results = []
        for city in cities:
            match city:
                case City(continent='Asia'):
                    results.append(city)
        return results


    def match_asian_countries():
        results = []
        for city in cities:
            match city:
                case City(continent='Asia', country=cc):
                    results.append(cc)
        return results

    print(match_asian_countries())
    print(match_asian_cities())