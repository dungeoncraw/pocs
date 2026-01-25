if __name__ == '__main__':
    s = 'café'
    print(s)
    print(len(s))
    b = s.encode('utf-8')
    print(b)
    print(len(b))
    print(b.decode('utf-8'))