import time


fn plain_iterations[iter: Int, a: SIMD[DType.float64, 8]]() -> SIMD[DType.float64, 8]:
    result = SIMD[DType.float64, 8](0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    for _i in range(iter):
        for _j in range(8):
            result[_j] += a[_j]
    return result


fn simd_operation[iter: Int, a: SIMD[DType.float64, 8]]() -> SIMD[DType.float64, 8]:
    result = SIMD[DType.float64, 8](0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    for _ in range(iter):
        result += a
    return result


@always_inline
fn benchmark_function[func: fn() raises -> SIMD[DType.float64, 8]](num_iters: Int) raises -> SIMD[DType.float64, 8]:
    var times: List[Float64] = List[Float64](capacity=num_iters)
    var accumulated_result = SIMD[DType.float64, 8]()
    
    for _ in range(num_iters):
        t0 = time.perf_counter_ns()
        var result = func()
        t_delta = time.perf_counter_ns() - t0
        times.append(Float64(t_delta) / 1e9)  # Convert to seconds
        accumulated_result = result  # Keep the result to prevent optimization
    
    # Print results similar to benchmark.run output format
    print("--------------------------------------------------------------------------------")
    print("Benchmark Report (s)")
    print("--------------------------------------------------------------------------------")
    
    var mean: Float64 = 0.0
    var total: Float64 = 0.0
    var fastest: Float64 = times[0]
    var slowest: Float64 = times[0]
    
    for i in range(len(times)):
        mean += times[i]
        total += times[i]
        if times[i] < fastest:
            fastest = times[i]
        if times[i] > slowest:
            slowest = times[i]
    
    mean /= Float64(num_iters)
    
    print("Mean: " + String(mean))
    print("Total: " + String(total))
    print("Iters: " + String(num_iters))
    print("Warmup Total: 0.0")
    print("Fastest Mean: " + String(fastest))
    print("Slowest Mean: " + String(slowest))
    print()
    print("Batch: 1")
    print("Iterations: " + String(num_iters))
    print("Mean: " + String(mean))
    print("Duration: " + String(total))
    print()
    
    return accumulated_result


fn main() raises:
    comptime a = SIMD[DType.float64, 8](1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
    comptime iter = Int(10_000_000)

    fn wrapper_plain() raises -> SIMD[DType.float64, 8]:
        return plain_iterations[iter, a]()
    
    fn wrapper_simd() raises -> SIMD[DType.float64, 8]:
        return simd_operation[iter, a]()

    var result1 = benchmark_function[wrapper_plain](7)
    var result2 = benchmark_function[wrapper_simd](10)
    
    print("Plain result: " + String(result1))
    print("SIMD result: " + String(result2))