if __name__ == '__main__':
    needles = {1, 3, 5, 7}
    haystack = {1, 2, 3, 4}

    found = len(needles & haystack)
    print(f'Needles: {needles}')
    print(f'Haystack: {haystack}')
    print(f'Found {found} common elements')
