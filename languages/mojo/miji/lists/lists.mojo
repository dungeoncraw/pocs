def main():
    # similar to python list literals
    my_list_of_integers = [1, 2, 3, 4, 5]
    var my_list_of_floats: List[Float64] = [0.125, 12.0, 12.625, -2.0, -12.0]
    var my_list_of_strings: List[String] = ["Mojo", "is", "awesome"]
    var my_list_of_list_of_integers: List[List[Int]] = [[1, 2], [3, 4], [5, 6]]

    print(my_list_of_integers[0])
    print(my_list_of_floats[0])
    print(my_list_of_strings[0])
    print(my_list_of_list_of_integers[0][0])

    # list copy
    lst1: List[List[Int]] = [[1]]
    lst2: List[List[Int]] = lst1.copy()
    print("Before modification:")
    print("lst1[0][0]: ", lst1[0][0])
    print("lst2[0][0]: ", lst2[0][0])

    lst2[0][0] = 42
    print("After modification:")
    print("lst1[0][0]: ", lst1[0][0])
    print("lst2[0][0]: ", lst2[0][0])
