import unicodedata, functools

nfc = functools.partial(unicodedata.normalize, 'NFC')

if __name__ == '__main__':
    s1 = 'café'
    s2 = 'cafe\u0301'
    print(s1, s2)
    print(s1 == s2)
    print(nfc(s1) == nfc(s2))