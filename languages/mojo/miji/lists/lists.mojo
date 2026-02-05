def main():
    # similar to python list literals
    my_list_of_integers = [1, 2, 3, 4, 5]
    var my_list_of_floats: List[Float64] = [0.125, 12.0, 12.625, -2.0, -12.0]
    var my_list_of_strings: List[String] = ["Mojo", "is", "awesome"]
    var my_list_of_list_of_integers: List[List[Int]] = [[1, 2], [3, 4], [5, 6]]
    
    first_element = my_list_of_integers[0]  # Accessing the first element
    sliced_list = my_list_of_integers[0:3]  # Slicing the first three elements
    print("First element:", first_element)
    print("Sliced list:", List(sliced_list))
    # is is immutable, so cannot change like i = i + 1
    for i in my_list_of_integers:
        print(i, end=" ")
    print("\n")
    
    for ref i in my_list_of_integers:
        i = i + 1

    for i in my_list_of_integers:
        print(i, end=" ")
    print("\n")
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


    lstOwner1: List[List[Int]] = [[1]]
    print("Before moving the list:")
    print("lstOwner1[0][0] =", lstOwner1[0][0])
    #   Move ownership from lstOwner1 to lstOwner2 so that lstOwner1 is no longer valid
    lstOwner2 = lstOwner1^
    print("After moving the list:")
    print("lstOwner2[0][0] =", lstOwner2[0][0])