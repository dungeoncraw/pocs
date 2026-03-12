def main():
    var a: Int = Int(input("Type the first integer `a`: "))
    var b: Int = Int(input("Type the second integer `b`: "))
    var c: Pointer[Int, origin = origin_of(a, b)]

    if a < b:
        c = Pointer[Int, origin = origin_of(a, b)](to=a)
    else:
        c = Pointer[Int, origin = origin_of(a, b)](to=b)

    print(
        "The first integer you give is", a, "at address", String(Pointer(to=a))
    )
    print(
        "The second integer you give is", b, "at address", String(Pointer(to=b))
    )
    print("The smaller of the two integers is", c[], "at address", String(c))