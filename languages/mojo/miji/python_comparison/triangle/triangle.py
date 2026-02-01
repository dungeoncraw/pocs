class Triangle:
    def __init__(self, side_a: float, side_b: float, side_c: float):
        self.side_a = side_a
        self.side_b = side_b
        self.side_c = side_c

        if (self.side_a + self.side_b <= self.side_c or
            self.side_a + self.side_c <= self.side_b or
            self.side_b + self.side_c <= self.side_a):
            raise ValueError("The provided sides do not form a valid triangle.")
    def area(self) -> float:
        s = (self.side_a + self.side_b + self.side_c) / 2
        return (s * (s - self.side_a) * (s - self.side_b) * (s - self.side_c)) ** 0.5
    def perimeter(self) -> float:
        return self.side_a + self.side_b + self.side_c
    
    def __str__(self) -> str:
        return f"Triangle(side_a={self.side_a}, side_b={self.side_b}, side_c={self.side_c})"
    
def main():
    # A valid triangle with sides 3, 4, and 5
    print("Creating a valid triangle with sides 3, 4, and 5:")
    triangle = Triangle(3, 4, 5)
    print(triangle)
    print(f"Area: {triangle.area()}")
    print(f"Perimeter: {triangle.perimeter()}")

    # An invalid triangle with sides 1, 2, and 3
    print("\nCreating an invalid triangle with sides 1, 2, and 3:")
    try:
        invalid_triangle = Triangle(1, 2, 3)
        print(invalid_triangle)
    except ValueError as e:
        print(f"Error: {e}")


main()