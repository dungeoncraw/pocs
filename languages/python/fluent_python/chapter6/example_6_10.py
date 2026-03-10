from copy import deepcopy

if __name__ == '__main__':
    a = [10, 20]
    b = [a, 30]
    a.append(b)
    print(a)
    c = deepcopy(a)
    print(c)