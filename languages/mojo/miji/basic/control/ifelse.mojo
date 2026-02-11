def main():
    var a = 10
    var b = "sunny"
    var c = "apple"

    var judge_sign = "negative" if a < 0 else "non-negative"
    var judge_weather = "nice" if b == "sunny" else "not nice"
    print(judge_sign)
    print(judge_weather)

    print("I am apple") if c == "apple" else print("I am not apple")

    var total = 0.0
    var numbers = [1.1, 2.2, 3.3, 4.4, 5.5]
    for number in numbers:
        total += number
    print("The sum of all items = ", total)

    var my_list = [1, 2, 3, 4, 5]
    for i in my_list:
        print(i, end=" ")
    else:
        print("Loop completed without break!")

    for i in my_list:
        if i == 3:
            break
        print(i, end=" ")
    else:
        print("Loop completed without break!")