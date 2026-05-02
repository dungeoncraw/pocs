class FenwickTree(private val size: Int) {
    private val tree = LongArray(size + 1)
    private var totalFreq: Long = 0

    fun update(index: Int, delta: Long) {
        var i = index + 1
        while (i <= size) {
            tree[i] += delta
            i += i and -i
        }
        totalFreq += delta
    }

    fun query(index: Int): Long {
        var i = index
        var sum: Long = 0
        while (i > 0) {
            sum += tree[i]
            i -= i and -i
        }
        return sum
    }

    fun getTotal(): Long = totalFreq

    fun findSymbol(count: Long): Int {
        var low = 0
        var high = size - 1
        var ans = 0
        while (low <= high) {
            val mid = (low + high) / 2
            if (query(mid + 1) > count) {
                ans = mid
                high = mid - 1
            } else {
                low = mid + 1
            }
        }
        return ans
    }
}
