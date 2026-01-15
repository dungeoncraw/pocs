if __name__ == '__main__':
    # three different inner lists
    board = [['_'] * 3 for i in range(3)]
    print(board)
    board[1][2] = 'X'
    print(board)
    # same memory pointer for the inner list
    weird_board = [['_'] * 3] * 3
    print(weird_board)
    weird_board[1][2] = 'O'
    print(weird_board)