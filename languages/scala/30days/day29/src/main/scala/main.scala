package com.tetokeguii.day29

import scopt.OParser

case class Config(
  foo: Int = -1,
  out: String = "./out",
  xyz: Boolean = false,
  libName: String = ""
)

@main
def main(args: String*): Unit = {
  val builder = OParser.builder[Config]
  val parser1 = {
    import builder.*
    OParser.sequence(
      programName("day29"),
      head("day29", "0.1"),
      opt[Int]('f', "foo")
        .action((x, c) => c.copy(foo = x))
        .text("foo is an integer property"),
      opt[String]('o', "out")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(out = x))
        .text("out is a required file property"),
      opt[Unit]("xyz")
        .action((_, c) => c.copy(xyz = true))
        .text("xyz is a boolean property"),
      help("help").text("prints this usage text")
    )
  }

  // OParser.parse returns Option[Config]
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      // do something
      println(s"Foo: ${config.foo}")
      println(s"Out: ${config.out}")
      println(s"XYZ: ${config.xyz}")
      println("Hello from Scala Fat Jar!")
    case _ =>
      // arguments are bad, error message will have been displayed
  }
}

