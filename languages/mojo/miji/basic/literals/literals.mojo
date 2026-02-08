def ex1():
    var a = 42  # `42` is inferred as `IntLiteral` and is converted to `Int` by default
    var b = 0x1F2D  # `0x1F2D` is inferred as `IntLiteral` and is converted to `Int` by default
    var c = 3.14  # `3.14` is inferred as `FloatLiteral` and is converted to `Float64` by default
    var d = 2.71828e-10  # `2.71828e-10` is inferred as `FloatLiteral` and is converted to `Float64` by default
    var e = [1, 2, 3]
    # `e` is inferred as `ListLiteral[IntLiteral]` and is converted to `List[Int]` by default
    var f = [[1.0, 1.1, 1.2], [2.0, 2.1, 2.2]]
    # `f` is inferred as `ListLiteral[ListLiteral[FloatLiteral]]` and
    # is converted to `List[List[Float64]]` by default
    var h = "Hello, World!"  # `e` is inferred as `StringLiteral` and is not converted by default

def ex2():
    var a: UInt8 = 42  # IntLiteral `42` is converted to `UInt8`
    var b: UInt32 = 0x1F2D  # IntLiteral `0x1F2D` is converted to `UInt32`
    var c: Float16 = 3.14  # FloatLiteral `3.14` is converted to `Float16`
    var d: Float32 = (
        2.71828e-10  # FloatLiteral `2.71828e-10` is converted to `Float32`
    )
    var e: List[Float32] = [1, 2, 3]
    # `ListLiteral[IntLiteral]` is converted to `List[Float32]`
    var f: String = "Hello, World!"  # `StringLiteral` is converted to `String`

def ex3():
    var a = UInt8(42)  # IntLiteral `42` is converted to `UInt8`
    var b = UInt32(0x1F2D)  # IntLiteral `0x1F2D` is converted to `UInt32`
    var c = Float16(3.14)  # FloatLiteral `3.14` is converted to `Float16`
    var d = Float32(
        2.71828e-10  # FloatLiteral `2.71828e-10` is converted to `Float32`
    )
    var e: List[Float32] = [1, 2, 3]
    # `ListLiteral[IntLiteral]` is converted to `List[Float32]`
    var f = String("Hello, World!")  # `StringLiteral` is converted to `String`

def main():
    ex1()
    ex2()
    ex3()