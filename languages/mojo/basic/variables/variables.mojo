def main():
    var `var`: Int = 1
    var `123`: Int = 123
    print(`var`)  # Using backticks to escape the keyword 'var'
    print(`123`)  # Using backticks to escape the numeric identifier '123'    a = 1
    b = 2.5
    c = "Hello, world!"
    d = [1, 2, 3]
    # this dont work as without var cannot set the type c: String = "Hello, world!"
    b = 5.5
    # this causes and error if try to assign a different type
    # a = "This will cause an error"
