import torch
import time

# Benchmark settings
N = 100_000_000
WARMUP = 10
ITERS = 100

device = "cuda"

if not torch.cuda.is_available():
    raise RuntimeError("CUDA GPU not available")


a = torch.arange(N, device=device, dtype=torch.float32)
b = torch.arange(N, device=device, dtype=torch.float32) * 0.5

# Warmup
for _ in range(WARMUP):
    c = a + b

torch.cuda.synchronize()

# Benchmark
start = time.perf_counter()

for _ in range(ITERS):
    c = a + b

torch.cuda.synchronize()
end = time.perf_counter()

elapsed = end - start
avg_ms = elapsed / ITERS * 1000

bytes_per_iter = N * 3 * 4
gb_per_sec = bytes_per_iter / (avg_ms / 1000) / 1e9

print("Python + PyTorch GPU vector add")
print(f"Elements: {N:,}")
print(f"Iterations: {ITERS}")
print(f"Average time: {avg_ms:.4f} ms")
print(f"Approx bandwidth: {gb_per_sec:.2f} GB/s")
print("Check:", c[10].item())