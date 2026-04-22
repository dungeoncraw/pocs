package com.tetokeguii.taxsystem

import com.tetokeguii.taxsystem.infrastructure.config.DatabaseConfig

@main
def main(): Unit = {
  val _ = DatabaseConfig.database
  println("Tax System application bootstrap placeholder")
}

