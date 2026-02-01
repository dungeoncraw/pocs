def main():
    var hexv = 0x1F2D # hex
    var binv = 0b1010 #binary
    var octv = -0o17 # octal
    var decv= 12345 # decimal
    var unv = 12345 # unsigned
    var consv = Int128(256) # constructor
    var tnv: Int8 = Int8(12) # type notation and constructor
    var simdv = SIMD[DType.int32, 1](10)
    print("Hex Value: ", hexv)
    print("Binary Value: ", binv)
    print("Octal Value: ", octv)
    print("Decimal Value: ", decv)
    print("Unsigned Value: ", unv)
    print("Constructor Value: ", consv)
    print("Type Notation Value: ", tnv)
    print("SIMD Value: ", simdv)


    # ================== conversion ==================
    var aUint8: UInt8 = 12
    var bInt8: Int8 = 23
    var cInt128: Int128 = 1234

    print("a + b =", UInt16(aUint8) + UInt16(bInt8))  # Type conversion to UInt16
    print("a - c =", Int128(aUint8) - cInt128)  # Type conversion to Int128
    print("b * c =", Int64(bInt8) * Int64(cInt128))  # Type conversion to Int64


    # ================== floatintg point ==================

    var f1: Float32 = 12.34
    var f2: Float64 = 56.78
    var f3: Float16 = 9.87
    var f4 = Float32(45.67) # constructor
    print("Float32 Value: ", f1)
    print("Float64 Value: ", f2)
    print("Float16 Value: ", f3)
    print("Constructor Float32 Value: ", f4)