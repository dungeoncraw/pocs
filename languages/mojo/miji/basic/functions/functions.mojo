def area_of_circle(radius: Float64) -> Float64:
    var pi = 3.1415
    return pi * radius * radius

fn area_of_square(side: Float64) raises -> Float64:
    if side < 0.0:
        raise "Side length cannot be negative"
    return side * side

def main():
    print("Area of Circle 1 with radius 1.0 is", area_of_circle(1.0))
    print("Area of Circle 2 with radius 2.0 is", area_of_circle(2.0))
    print("Area of Circle 3 with radius 3.0 is", area_of_circle(3.0))
    print("Area of Square with side 1.0 is", area_of_square(1.0))
    print("Area of Square with side 2.0 is", area_of_square(2.0))
    print("Area of Square with side 3.0 is", area_of_square(3.0))
    print("Area of Square with side -1.0 is", area_of_square(-1.0))  # This will raise an error