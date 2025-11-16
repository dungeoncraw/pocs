# day 15 - macros

The goal is to play with macros and see the inline operations in action.

To show the AST (Abstract Syntax Tree), include in build.sbt the following line:
```
scalacOptions += "-Xprint:inlining"
```

Then run `sbt compile` to see the AST in the console.


## Example

so for the following code
```scala 3
inline def power(x: Double, inline n: Int): Double =
  if (n == 0) 1.0
  else if (n % 2 == 1) x * power(x, n - 1)
  else power(x * x, n / 2)

inline def half(x: Any): Any =
  inline x match
    case x: Int => x / 2
    case x: String => x.substring(0, x.length / 2)
@main
def main(): Unit = {
  println(power(2, 2))
  println(half(2))
  println(half("hello world"))
}
```

generates the following AST
```scala
[info] package com.tetokeguii.day15 {
[info]   final lazy module val main$package: com.tetokeguii.day15.main$package =
[info]     new com.tetokeguii.day15.main$package()
[info]   @SourceFile("src/main/scala/main.scala") final module class main$package()
[info]      extends Object() { this: com.tetokeguii.day15.main$package.type =>
[info]     private def writeReplace(): AnyRef =
[info]       new scala.runtime.ModuleSerializationProxy(
[info]         classOf[com.tetokeguii.day15.main$package.type])
[info]     inline def power(x: Double, inline n: Int): Double =
[info]       (if n == 0 then 1.0d else
[info]         if n % 2 == 1 then x * com.tetokeguii.day15.power(x, n - 1) else
[info]           com.tetokeguii.day15.power(x * x, n / 2)
[info]       ):Double
[info]     inline def half(x: Any): Any =
[info]       (inline x match
[info]         {
[info]           case x @ _:Int =>
[info]             x / 2
[info]           case x @ _:String =>
[info]             x.substring(0, x.length() / 2)
[info]         }
[info]       ):Any
[info]     @main def main(): Unit =
[info]       {
[info]         println(4.0d:Double:Double)
[info]         println(1:Any)
[info]         println(
[info]           {
[info]             val x: ("hello world" : String) = "hello world"
[info]             x.substring(0, x.length() / 2)
[info]           }:Any
[info]         )
[info]       }
[info]   }
[info]   @SourceFile("src/main/scala/main.scala") final class main() extends Object() {
[info]     <static> def main(args: Array[String]): Unit =
[info]       try com.tetokeguii.day15.main() catch
[info]         {
[info]           case error @ _:scala.util.CommandLineParser.ParseError =>
[info]             scala.util.CommandLineParser.showError(error)
[info]         }
[info]   }
[info] }
```