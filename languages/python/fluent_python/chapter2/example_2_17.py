if __name__ == '__main__':
    fruits = ['grape', 'raspberry', 'apple','banana']
    sorted(fruits) # create a new list sorted by name
    sorted(fruits, reverse=True) # create a new list sorted by name in reverse order
    sorted(fruits, key=len) # create a new list sorted by length of name
    sorted(fruits, key=len, reverse=True) # create a new list sorted by length of name in reverse order
    print(fruits) # so no changes on the original fruit list
    fruits.sort() # sort in place
    print(fruits) # now the original list is sorted