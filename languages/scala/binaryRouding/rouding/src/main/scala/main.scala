package com.tetokeguii.binary

@main
def main(): Unit = {
  println("=====THIS IS TO UNDERSTAND BINARY ROUNDING=========")
  val a = 0.1f
  val b = 0.2f
  val c = a + b

  def bitsOf(f: Float): String =
    String.format("%32s", Integer.toBinaryString(java.lang.Float.floatToIntBits(f)))
      .replace(' ', '0')

  println(bitsOf(0.1f))
  println(bitsOf(0.2f))
  println(bitsOf(0.1f + 0.2f))

  val x = BigDecimal("0.1")
  val y = BigDecimal("0.2")
  val z = x + y
  println("=====BIGDECIMAL STRING=========")
  println(x)
  println(y)
  println(z)
  println(z.underlying.unscaledValue)
  println(z.underlying.scale)

  println("=====BIGDECIMAL FLOAT BINARY=========")
  // stores correctly
  val biga = BigDecimal("1000000.10210212000000000000000000355")
  // binary rounding the value, 1000000.10210212
  val bigb = BigDecimal(1000000.10210212000000000000000000355)

  println(s"biga = $biga")
  println(s"bigb = $bigb")

  println(s"biga unscaled = ${biga.underlying.unscaledValue}")
  println(s"biga scale    = ${biga.underlying.scale}")

  println(s"bigb unscaled = ${bigb.underlying.unscaledValue}")
  println(s"bigb scale    = ${bigb.underlying.scale}")
}

