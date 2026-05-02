import java.util.*

class AdaptiveArithmeticCoder(private val alphabetSize: Int) {
    private val fenwickTree = FenwickTree(alphabetSize)
    private val precision = 32
    private val maxRange = (1L shl precision) - 1
    private val half = 1L shl (precision - 1)
    private val quarter = 1L shl (precision - 2)
    private val threeQuarters = 3 * quarter

    init {
        for (i in 0..<alphabetSize) {
            fenwickTree.update(i, 1)
        }
    }

    fun encode(symbols: IntArray): BitSet {
        var low = 0L
        var high = maxRange
        val bits = mutableListOf<Boolean>()
        var underflowBits = 0

        fun outputBit(bit: Boolean) {
            bits.add(bit)
            while (underflowBits > 0) {
                bits.add(!bit)
                underflowBits--
            }
        }

        for (sym in symbols) {
            val total = fenwickTree.getTotal()
            val range = high - low + 1
            high = low + (range * fenwickTree.query(sym + 1) / total) - 1
            low += (range * fenwickTree.query(sym) / total)
            
            fenwickTree.update(sym, 1)

            while (true) {
                if (high < half) {
                    outputBit(false)
                } else if (low >= half) {
                    outputBit(true)
                    low -= half
                    high -= half
                } else if (low >= quarter && high < threeQuarters) {
                    underflowBits++
                    low -= quarter
                    high -= quarter
                } else {
                    break
                }
                low = (low shl 1) and maxRange
                high = ((high shl 1) or 1) and maxRange
            }
        }

        underflowBits++
        if (low < quarter) outputBit(false) else outputBit(true)

        val bitSet = BitSet(bits.size)
        for (i in bits.indices) {
            if (bits[i]) bitSet.set(i)
        }
        return bitSet
    }

    fun decode(bitSet: BitSet, numSymbols: Int, totalBits: Int): IntArray {
        val decoderTree = FenwickTree(alphabetSize)
        for (i in 0..<alphabetSize) {
            decoderTree.update(i, 1)
        }

        var low = 0L
        var high = maxRange
        var value = 0L
        var bitPointer = 0

        for (i in 0..<precision) {
            if (bitPointer < totalBits) {
                if (bitSet.get(bitPointer)) {
                    value = value or (1L shl (precision - 1 - i))
                }
                bitPointer++
            }
        }

        val decodedSymbols = IntArray(numSymbols)
        for (i in 0..<numSymbols) {
            val total = decoderTree.getTotal()
            val range = high - low + 1
            val count = ((value - low + 1) * total - 1) / range
            
            val sym = decoderTree.findSymbol(count)
            decodedSymbols[i] = sym

            high = low + (range * decoderTree.query(sym + 1) / total) - 1
            low += (range * decoderTree.query(sym) / total)
            
            decoderTree.update(sym, 1)

            while (true) {
                if (high < half) {
                    // Lower half
                } else if (low >= half) {
                    low -= half
                    high -= half
                    value -= half
                } else if (low >= quarter && high < threeQuarters) {
                    low -= quarter
                    high -= quarter
                    value -= quarter
                } else {
                    break
                }
                low = (low shl 1) and maxRange
                high = ((high shl 1) or 1) and maxRange
                
                val nextBit = if (bitPointer < totalBits && bitSet.get(bitPointer)) 1L else 0L
                value = ((value shl 1) or nextBit) and maxRange
                bitPointer++
            }
        }
        return decodedSymbols
    }
}
