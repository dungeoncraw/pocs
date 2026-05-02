
fun main() {
    val input = "mississippi"
    println("Input string: $input")
    
    val symbols = input.map { it.code }.toIntArray()
    val alphabetSize = 256
    
    println("\n--- FENWICK-BASED ARITHMETIC CODING ---")
    val fenwickCoder = AdaptiveArithmeticCoder(alphabetSize)
    val startFenwick = System.nanoTime()
    val fenwickBits = fenwickCoder.encode(symbols)
    val endFenwick = System.nanoTime()
    
    val fenwickDecoder = AdaptiveArithmeticCoder(alphabetSize)
    val decodedFenwick = fenwickDecoder.decode(fenwickBits, symbols.size, 10000)
    val outputFenwick = decodedFenwick.map { it.toChar() }.joinToString("")
    println("Encoded size: ${fenwickBits.length()} bits")
    println("Time: ${(endFenwick - startFenwick) / 1000} us")
    println("Decoded matches: ${input == outputFenwick}")
}