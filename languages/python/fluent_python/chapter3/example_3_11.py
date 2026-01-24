if __name__ == '__main__':
    d = dict(a=10, b=20, c=30)
    values = d.values()
    print(values)
    print(len(values))
    print(list(values))
    print(reversed(values))
    try:
        # this causes an error as dict_values object is not subscriptable as it is read-only
        print(values[0])
    except TypeError as e:
        print(f"TypeError: {e}")
    d['z'] = 99
    print(d)
    print(values)