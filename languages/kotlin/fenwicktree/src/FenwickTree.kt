class FenwickTree(private val n: Int) {
    private val bit = LongArray(n + 1)

    fun add(index: Int, delta: Long) {
        var i = index
        while (i <= n) {
            bit[i] += delta
            i += i and -i
        }
    }

    fun sum(index: Int): Long {
        var i = index
        var res = 0L
        while (i > 0) {
            res += bit[i]
            i -= i and -i
        }
        return res
    }

    fun rangeSum(left: Int, right: Int): Long {
        if (left > right) return 0L
        return sum(right) - sum(left - 1)
    }
}