package com.tetokeguii.day20

class Box[T](value: T) {
  def get: T = value
  def isEmpty: Boolean = false
}
def identity[T](x: T): T = x
def swap[A, B](pair: (A, B)): (B, A) = (pair._2, pair._1)
