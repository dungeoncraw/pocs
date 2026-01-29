package com.dungeoncraw.func_examples

import munit.FunSuite

class ResourceExampleSuite extends FunSuite:

  test("UserProfile implements Resource fields") {
    val user = UserProfile("USR-101", "Alice Smith", "alice@example.com")
    assertEquals(user.id, "USR-101")
    assertEquals(user.name, "Alice Smith")
  }

  test("SystemConfig implements Resource fields") {
    val cfg = SystemConfig("CFG-99", "MaxRetries", "5")
    assertEquals(cfg.id, "CFG-99")
    assertEquals(cfg.name, "MaxRetries")
  }

  test("Encryptable encrypt wraps data") {
    val user = UserProfile("USR-101", "Alice Smith", "alice@example.com")
    assertEquals(user.encrypt("abc"), "ENCRYPTED(abc)")
  }

  test("Hashable.hash returns the same as String.hashCode.toString") {
    val user = UserProfile("USR-101", "Alice Smith", "alice@example.com")
    val cfg  = SystemConfig("CFG-99", "MaxRetries", "5")

    assertEquals(user.hash("hello"), "hello".hashCode.toString)
    assertEquals(cfg.hash("hello"), "hello".hashCode.toString)
  }

  test("GlobalRegistry has expected id and name") {
    assertEquals(GlobalRegistry.id, "ROOT_001")
    assertEquals(GlobalRegistry.name, "Global Resource Registry")
  }