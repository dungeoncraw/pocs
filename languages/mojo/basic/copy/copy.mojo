def main():
    var a = 1
    var b = a.copy()
    print("a =", a)
    print("b =", b)
    print(
        "a and b has the same address:",
        Pointer(to=a) == Pointer(to=b),
    )

    var str1: String = "Hello"
    var str2 = (
        str1.copy()
    )
    print("str1 =", str1)
    print("str2 =", str2)
    print(
        "str1 and str2 has the same address:",
        Pointer(to=str1) == Pointer(to=str2),
    )

    var lst1: List[Int] = [1, 2, 3]
    var lst2 = lst1.copy()
    
    print("lst1 =", end=" ")
    for i in lst1:
        print(i, end=", ")
    print("\nlst2 =", end=" ")
    for i in lst2:
        print(i, end=", ")
    print(
        "\nlst1 and lst2 has the same address:",
        Pointer(to=lst1) == Pointer(to=lst2),
    )
    # implicity copy for "cheap" types
    var aa = 1
    var bbb = aa  # Implicitly copy the value of `aa` into a new variable `bbb`
    # This is equivalent to `var bbb = aa.copy()`
    print("aa =", aa)
    print("bbb =", bbb)
    print(
        "aa and bbb has the same address:",
        Pointer(to=aa) == Pointer(to=bbb),
    )

    var str11: String = "Hello"
    var str22 = (
        str11  # Implicitly copy the value of `str11` into a new variable `str22`
    )
    # This is equivalent to `var str22 = str11.copy()`
    print("str11 =", str11)
    print("str22 =", str22)
    print(
        "str11 and str22 has the same address:",
        Pointer(to=str11) == Pointer(to=str22),
    )

    var bool11: Bool = True
    var bool22 = bool11  # Implicitly copy the value of `bool11` into a new variable `bool22`
    # This is equivalent to `var bool22 = bool11.copy()`
    print("bool11 =", bool11)
    print("bool22 =", bool22)
    print(
        "bool11 and bool22 has the same address:",
        Pointer(to=bool11) == Pointer(to=bool22),
    )

    var lstMv1: List[Int] = [1, 2, 3]
    var lstMv2 = lstMv1^
    # Move the value of `lstMv1` into a new variable `lstMv2`
    print("lstMv2 =", end=" ")
    for i in lstMv2:
        print(i, end=", ")

    var strMv1: String = "Hello"
    var strMv2 = strMv1^  # Move the value of `strMv1` into a new variable `strMv2`
    print()
    print("strMv2 =", strMv2)