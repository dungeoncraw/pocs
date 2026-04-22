package com.tetokeguii.taxsystem.infrastructure.config

import slick.jdbc.JdbcBackend.Database

object DatabaseConfig {
  lazy val database: Database = Database.forConfig("tax-system.db")
}
