package main

import (
	"fmt"
	"math/rand/v2"
	"runtime"
	"sync"
	"time"
)

func main() {
	const total = 100

	fmt.Printf("CPUs: %d\n", runtime.NumCPU())
	fmt.Printf("Starting %d goroutines...\n", total)

	start := time.Now()

	var wg sync.WaitGroup
	results := make(chan int, total)

	for i := range total {
		id := i + 1
		wg.Go(func() {
			// Simulate concurrent work
			time.Sleep(20 * time.Millisecond)
			randomMultiplier := rand.IntN(10) + 1
			results <- id * randomMultiplier
		})
	}

	go func() {
		wg.Wait()
		close(results)
	}()

	sum := 0
	for value := range results {
		sum += value
	}

	fmt.Printf("All goroutines completed in %v\n", time.Since(start))
	fmt.Printf("Sum of random goroutine values: %d\n", sum)
}
