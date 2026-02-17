# src/basic/structs/complex_number.mojo
struct Complex(Writable):
    """Complex number with real and imaginary parts."""

    var real: Float64
    """Real part of the complex number."""
    var imag: Float64
    """Imaginary part of the complex number."""

    fn __init__(out self, real: Float64 = 0.0, imag: Float64 = 0.0):
        """Initializes a complex number with real and imaginary parts.

        Args:
            real: The real part of the complex number (default is 0.0).
            imag: The imaginary part of the complex number (default is 0.0).
        """
        self.real = real
        self.imag = imag

    fn write_to[T: Writer](self, mut writer: T):
        """Writes the complex number to a writer."""
        if self.imag < 0:
            writer.write(self.real, self.imag, "i")
        else:
            writer.write(self.real, "+", self.imag, "i")


fn main():
    var c1 = Complex(3.0, 4.0)
    var c2 = Complex(1.0, -2.0)
    var c3 = Complex()

    print("Complex number c1:", c1)
    print("Complex number c2:", c2)
    print("Complex number c3:", c3)