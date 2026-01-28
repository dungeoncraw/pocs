import time

fn main() raises:
    start_time = time.perf_counter()
    print("Nine-nine Multiplication Table")
    for i in range(1, 10):
        for j in range(i, 10):
            print(String("{} * {} = {}").format(i, j, i * j), end="\t")
        print()
    end_time = time.perf_counter()
    elapsed_ms = (end_time - start_time) * 1000
    print("\nStart time:", start_time)
    print("End time:", end_time)
    print("Elapsed time:", elapsed_ms, "ms")
