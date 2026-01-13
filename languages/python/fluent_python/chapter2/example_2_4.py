if __name__ == '__main__':
    colors = ['red', 'orange', 'yellow', 'green', 'blue', 'violet']
    sizes = ['S', 'M', 'L']
    # order by color
    tshirts = [(color, size) for color in colors for size in sizes]
    print(tshirts)
    # same as list comprehension above, but print each item instead of whole list
    for color in colors:
        for size in sizes:
            print(color, size)

    # order by size
    tshirts = [(color, size) for size in sizes for color in colors]
    print(tshirts)