# src/basic/structs/complex_number.py
class Complex:
    """Complex number with real and imaginary parts."""
    
    # Define fields (attributes)
    # These are not mandatory in Python, but good for documentation.
    real: float  # Real part of the complex number
    imag: float  # Imaginary part of the complex number

    def __init__(self, real: float = 0.0, imag: float = 0.0):
        """Initialize a complex number with real and imaginary parts."""
        self.real = real
        self.imag = imag

    def __str__(self) -> str:
        """Return a string representation of the complex number."""
        if self.imag < 0:
            return f"{self.real}{self.imag}i"
        else:
            return f"{self.real}+{self.imag}i"
    
def main():
    c1 = Complex(3.0, 4.0)
    c2 = Complex(1.0, -2.0)
    c3 = Complex()
    
    print("Complex Number 1:", c1)
    print("Complex Number 2:", c2)
    print("Complex Number 3:", c3)

main()