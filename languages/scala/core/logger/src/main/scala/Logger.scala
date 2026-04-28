package com.tetokeguii.logger

trait Logger {
  def debug(message: String): Unit
  def info(message: String): Unit
  def warn(message: String): Unit
  def error(message: String): Unit
}