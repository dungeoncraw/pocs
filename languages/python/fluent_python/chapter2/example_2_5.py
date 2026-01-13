import array

if __name__ == '__main__':
    symbols = '$¢£¥€¤'
    # using iterator tuple constructor
    tuples = tuple(ord(symbol) for symbol in symbols)
    print(tuples[0])

    arr = array.array('I', (ord(symbol) for symbol in symbols))
    print(arr)