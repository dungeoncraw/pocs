package com.tetokeguii.day20

trait Producer[+T] {
  def get(): T
}
