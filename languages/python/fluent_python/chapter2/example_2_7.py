if __name__ == '__main__':
    lax_coordinates = (33.9425, -118.408056)
    city, year, pop, chg, area = ('Tokyo', 2003, 32_450, 0.66, 8014)
    traveler_ids = [('USA', '31195855'), ('BRA', 'CE342567'), ('ESP', 'XDA205856')]
    for passport in traveler_ids:
        print('%s/%s' % passport)
        # print(f'{passport[0]}/{passport[1]}')

    for country, _ in traveler_ids:
        print(country)

    a = (10, 'alpha', [1, 2])
    b = (10, 'alpha', [1, 2])
    print(a == b) # true
    b[-1].append(3)
    print(a == b) # false

    a, b, *rest = range(5)
    print(a, b, rest)
    a, b, *rest = range(2)
    print(a, b, rest)