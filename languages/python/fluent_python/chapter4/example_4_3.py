import array

if __name__ == '__main__':
    numbers = array.array('i', [1, 2, 3])
    print(numbers)
    octets = bytes(numbers)
    print(octets)
    for codec in ['latin_1', 'utf_8', 'utf_16']:
        print(codec, 'El Niño'.encode(codec), sep='\t')