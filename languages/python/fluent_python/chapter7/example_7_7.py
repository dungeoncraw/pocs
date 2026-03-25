if __name__ == '__main__':
    fruits = ['strawberry', 'fig', 'apple', 'cherry', 'raspberry', 'banana']
    print(sorted(fruits, key=lambda word: word[::-1]))