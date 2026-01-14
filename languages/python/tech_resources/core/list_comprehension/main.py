import dis

def exemple():
    simple_list = [x * 2 for x in [1, 2, 3]]
    return simple_list

if __name__ == '__main__':
    quad = [x ** 2 for x in range(10)]
    print(quad)

    names = ["Ana", "Bruno", "Amanda", "Carlos", "Arthur", "Beatriz"]

    names_starting_with_a = [nome.upper() for nome in names if nome.startswith("A")]

    print(names_starting_with_a)

    dis.dis(exemple)