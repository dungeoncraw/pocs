package com.tetokeguii.logger

import munit.FunSuite
import java.nio.file.Files
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class LogSinkTests extends FunSuite {

  test("ConsoleSink should print to stdout") {
    val outContent = new ByteArrayOutputStream()
    val originalOut = System.out
    System.setOut(new PrintStream(outContent))
    
    try {
      val sink = new ConsoleSink()
      val event = LogEvent(LogLevel.Info, "console test", 123456789L)
      sink.write(event)
      
      val output = outContent.toString.trim
      assert(output.contains("[Info]"))
      assert(output.contains("123456789"))
      assert(output.contains("console test"))
    } finally {
      System.setOut(originalOut)
    }
  }

  test("FileSink should write to file") {
    val tempFile = Files.createTempFile("log-test", ".log")
    try {
      val sink = new FileSink(tempFile.toString)
      val event = LogEvent(LogLevel.Error, "file test", 987654321L)
      sink.write(event)
      
      val content = Files.readString(tempFile)
      assert(content.contains("[Error]"))
      assert(content.contains("987654321"))
      assert(content.contains("file test"))
    } finally {
      Files.deleteIfExists(tempFile)
    }
  }
}
