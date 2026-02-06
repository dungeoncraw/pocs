def main():
    s1: str = "Hello, world!"
    s2 = str("Hello, world!")
    s3 = "Hello, Mojo v25.5!"
    print(s1)
    print(s2)
    print(s3)
    my_string = str("Hello, world! 你好，世界！")
    for char in my_string:
        print(f"char: {char}\n")

main()