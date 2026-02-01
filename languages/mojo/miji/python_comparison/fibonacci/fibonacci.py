import time

def fib(n: int) -> int:
    if n <= 1:
        return n
    return fib(n - 1) + fib(n - 2)

def main():
    start_time = time.time()
    print("Start time:", start_time)
    for i in range(40):
        print(fib(i), end=", ")
    end_time = time.time()
    elapsed_ms = (end_time - start_time) * 1000
    print()
    print("End time:", end_time)
    print(f"Elapsed time: {elapsed_ms:.2f} ms")

main()