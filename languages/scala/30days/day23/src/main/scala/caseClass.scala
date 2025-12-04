package com.tetokeguii.day23

case class Database(name: String, host: String)
case class Logger(level: String)
case class Config(
                   appName: String,
                   version: String,
                   environment: String,
                   debug: Boolean
                 )

case class User(
                 id: String,
                 name: String,
                 role: String
               )