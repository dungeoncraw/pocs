struct Matrix[dtype: DType = DType.float64]:
    """A 2D matrix type.
    
    Parameters:
        dtype: The data type of the matrix elements. Defaults to `DType.float64`.
    """

    var data: List[Scalar[dtype]]
    """The elements of the matrix stored in row-major layout (C-contiguous)."""
    var size: Int
    """The total number of elements in the matrix."""
    var shape: Tuple[Int, Int]
    """The shape of the matrix as a tuple (rows, cols)."""
    var strides: Tuple[Int, Int]
    """The strides of the matrix in memory (row stride, col stride)."""