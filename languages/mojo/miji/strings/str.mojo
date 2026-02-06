def main():
    var s1 = String("Hello, world!")
    var s2: String = "Hello, Mojo!"
    var s3 = "Hello, Mojo v25.5!"
    print(s1)
    print(s2)
    print(s3)
    var s = (
        "This is a string literal "
        "that spans multiple lines."
    )
    print(s)
    # no f-string support yet, neither format specifiers like {0:.2f}
    var a = String("Today is {} {} {}").format(1, "Janurary", 2023)
    var b = String("{0} plus {1} equals {2}").format(1.1, 2.34, 3.45)
    var c = "{0} apples plus {1} oranges is {2}".format(3, 2, "nonsense")
    print(a)
    print(b)
    print(c)

    my_string = String("Hello, world! 你好，世界！")
    # iterate over codepoints not characters
    for char in my_string.codepoints():
        print(String("char: {}\n").format(char), end="")


    var s4 = String("你好shìjiè😀🇨🇳")
    var idx = 0
    print("Index | Binary       | Decimal | Hexadecimal")
    for i in s4.as_bytes():
        var byte_dec = Int(i)
        var byte_bin = bin(byte_dec)
        var byte_hex = hex(byte_dec)
        print(idx, "    | ", byte_bin, " | ", byte_dec, "   | ", byte_hex)
        idx += 1
