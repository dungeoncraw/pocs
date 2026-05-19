from std.sys import has_accelerator, exit
from std.gpu.host import DeviceContext
from std.gpu import block_dim, block_idx, thread_idx
from std.time import perf_counter_ns
from std.math import ceildiv


comptime dtype = DType.float32
comptime N = 100_000_000
comptime BLOCK_SIZE = 256
comptime NUM_BLOCKS = ceildiv(N, BLOCK_SIZE)
comptime WARMUP = 10
comptime ITERS = 100


def vector_add(
    a: UnsafePointer[Float32, MutAnyOrigin],
    b: UnsafePointer[Float32, MutAnyOrigin],
    c: UnsafePointer[Float32, MutAnyOrigin],
    size: Int,
):
    idx = block_idx.x * block_dim.x + thread_idx.x

    if idx < size:
        c[idx] = a[idx] + b[idx]


def main() raises:
    comptime if not has_accelerator():
        print("No compatible GPU found")
        exit(0)
    else:
        ctx = DeviceContext()

        a = ctx.enqueue_create_buffer[dtype](N)
        b = ctx.enqueue_create_buffer[dtype](N)
        c = ctx.enqueue_create_buffer[dtype](N)

        kernel = ctx.compile_function[vector_add, vector_add]()

        # Warmup
        for _ in range(WARMUP):
            ctx.enqueue_function(
                kernel,
                a,
                b,
                c,
                N,
                grid_dim=NUM_BLOCKS,
                block_dim=BLOCK_SIZE,
            )

        ctx.synchronize()

        start = perf_counter_ns()

        for _ in range(ITERS):
            ctx.enqueue_function(
                kernel,
                a,
                b,
                c,
                N,
                grid_dim=NUM_BLOCKS,
                block_dim=BLOCK_SIZE,
            )

        ctx.synchronize()
        end = perf_counter_ns()

        elapsed_ns = end - start
        avg_ms = Float64(elapsed_ns) / Float64(ITERS) / 1_000_000.0

        # Each iteration reads a and b, writes c: 3 arrays of float32
        bytes_per_iter = N * 3 * 4
        gb_per_sec = Float64(bytes_per_iter) / (avg_ms / 1000.0) / 1_000_000_000.0

        print("Mojo custom GPU vector add")
        print("Elements:", N)
        print("Iterations:", ITERS)
        print("Average time:", avg_ms, "ms")
        print("Approx bandwidth:", gb_per_sec, "GB/s")