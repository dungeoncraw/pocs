import collections

class StrKeyDict(collections.UserDict):
    def __missing__(self, key):
        if isinstance(key, str):
            print(f"KeyError from class: {key}")
            raise KeyError(key)
        return self[str(key)]
    def __contains__(self, key):
        return str(key) in self.data

    def __setitem__(self, key, value):
        self.data[str(key)] = value


if __name__ == '__main__':
    d = StrKeyDict()
    d['key'] = 'value'
    print(d['key'])
    print('key' in d)
    print('key2' in d)
    # calling missing here converts the key to string
    d[2] = '2'
    print(d[2])

    try:
        print(d['key2'])
    except KeyError as e:
        print(f"KeyError: {e}")