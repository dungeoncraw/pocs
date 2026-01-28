import time

def main():
    start_time = time.time()
    print("Nine-nine Multiplication Table")
    for i in range(1, 10):
        for j in range(i, 10):
            print("{} * {} = {}".format(i, j, i*j), end="\t")
        print()
    end_time = time.time()
    elapsed_ms = (end_time - start_time) * 1000
    print(f"\nStart time: {start_time:.3f}")
    print(f"End time: {end_time:.3f}")
    print(f"Elapsed time: {elapsed_ms:.2f} ms")

main()