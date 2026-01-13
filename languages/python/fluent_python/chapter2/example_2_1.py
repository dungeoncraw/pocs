
if __name__ == "__main__":
    symbols = '$¢£¥€¤'
    codes = []
    for symbol in symbols:
        codes.append(ord(symbol))
    print(codes)