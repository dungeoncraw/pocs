import time

# Capital I for Int
fn fib(n: Int) -> Int:
    if n <= 1:
        return n
    return fib(n - 1) + fib(n - 2)

fn main() raises:
    start_time = time.perf_counter()
    print("Start time:", start_time)
    for i in range(40):
        print(fib(i), end=", ")
    end_time = time.perf_counter()
    elapsed_ms = (end_time - start_time) * 1000
    print()
    print("End time:", end_time)
    print("Elapsed time:", elapsed_ms, "ms")