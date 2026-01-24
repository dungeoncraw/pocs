from types import MappingProxyType

if __name__ == '__main__':
    d = {1: 'A'}
    # make d_proxy a read-only view of d
    d_proxy = MappingProxyType(d)
    print(d_proxy[1])
    try:
        d_proxy[2] = 'B'
    except TypeError as e:
        print(f"TypeError: {e}")
    d[2] = 'B'

    print(d_proxy[2])
    print(d_proxy)