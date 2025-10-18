// scala
package com.tetokeguii.day02

def customMap[A, B](f: B => A, xs: List[B]): List[A] = for (x <- xs) yield f(x)

object Benchmark {
  private val f: Int => Int = x => x * x

  private def runStd(xs: List[Int]): Long =
    xs.map(f).foldLeft(0L)(_ + _.toLong)

  private def runCustom(xs: List[Int]): Long =
    customMap(f, xs).foldLeft(0L)(_ + _.toLong)

  private def benchmark(name: String, run: List[Int] => Long, xs: List[Int], warmups: Int = 5, iters: Int = 20): Unit = {
    // warmup for JIT optimization
    for (_ <- 0 until warmups) run(xs)

    val times = for (_ <- 0 until iters) yield {
      val t0 = System.nanoTime()
      val _ = run(xs)
      System.nanoTime() - t0
    }

    val avgNs = times.sum / times.length
    println(s"$name: avg = ${avgNs / 1e6} ms (over $iters runs, list size = ${xs.length})")
  }

  @main
  def mainBenchmark(): Unit = {
    val size = 100000
    val xs = (1 to size).toList

    benchmark("std map", _ => runStd(xs), xs = xs, warmups = 20, iters = 100)
    // std map: avg = 1.410349 ms (over 100 runs, list size = 100000)

    benchmark("custom map", _ => runCustom(xs), xs = xs, warmups = 20, iters = 100)
    // custom map: avg = 1.055972 ms (over 100 runs, list size = 100000)
  }
}