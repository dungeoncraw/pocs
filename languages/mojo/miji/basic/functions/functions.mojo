from memory import Pointer

def area_of_circle(radius: Float64) -> Float64:
    var pi = 3.1415
    return pi * radius * radius

fn area_of_square(side: Float64) raises -> Float64:
    if side < 0.0:
        raise "Side length cannot be negative"
    return side * side

def print_list_of_string(read a: List[String]):
    # `a` is a read-only reference of the list passed into the function
    print("[", end="")
    for i in range(len(a)):
        if i < len(a) - 1:
            print(String('"{}"').format(a[i]), end=", ")
        else:
            print(String('"{}"').format(a[i]), end="]\n")

# fn changeit(read some: List[Int]) -> List[Int]:
#     some[0] = 100  # This will raise an error because `some` is a read-only reference

def changeit(mut a: Int8):
    a = 10
    print(
        String(
            "In function call: argument `a` is of the value {} and the address {}"
        ).format(a, String(Pointer(to=a)))
    )
# using var creates a copy of the argument passed in
def changeitVar(var a: Int8):
    print(
        String(
            "Within function call: argument `a` is of the value {} and the address {}"
        ).format(a, String(Pointer(to=a)))
    )
    a = 10
    print("Within function call: change value of a to 10 with `a = 10`")
    print(
        String(
            "Within function call: argument `a` is of the value {} and the address {}"
        ).format(a, String(Pointer(to=a)))
    )

fn bigger(a: Int) -> Int:
    return a

fn bigger(a: Int, b: Int) -> Int:
    return a if a > b else b

def main():
    print("Area of Circle 1 with radius 1.0 is", area_of_circle(1.0))
    print("Area of Circle 2 with radius 2.0 is", area_of_circle(2.0))
    print("Area of Circle 3 with radius 3.0 is", area_of_circle(3.0))
    print("Area of Square with side 1.0 is", area_of_square(1.0))
    print("Area of Square with side 2.0 is", area_of_square(2.0))
    print("Area of Square with side 3.0 is", area_of_square(3.0))
    # print("Area of Square with side -1.0 is", area_of_square(-1.0))  # This will raise an error
    var lst = List[String]()
    lst.append("Hello")
    lst.append("World")
    var new_lst = print_list_of_string(lst)
    var a = List[Int]()
    a.append(1)
    a.append(2)
    a.append(3)
    a.append(4)
    a.append(5)
    var x: Int8 = 5
    print(
        String(
            "Before change:    variable `x` is of the value {} and the address {}"
        ).format(x, String(Pointer(to=x)))
    )
    changeit(x)
    print(
        String(
            "Before change:    variable `x` is of the value {} and the address {}"
        ).format(x, String(Pointer(to=x)))
    )

    var y: Int8 = 5
    print(
        String(
            "Before function call: variable `y` is of the value {} and the address {}"
        ).format(y, String(Pointer(to=y)))
    )
    changeitVar(y)
    print(
        String(
            "After function call: variable `y` is of the value {} and the address {}"
        ).format(y, String(Pointer(to=y)))
    )
    print(bigger(1, 2))
    print(bigger(3))