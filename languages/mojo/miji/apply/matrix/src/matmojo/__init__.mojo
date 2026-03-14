fn __init__(out self, data: List[Scalar[dtype]], shape: Tuple[Int, Int]) raises:
    """Initialize the matrix with data and shape.
    
    Args:
        data: A list of elements to initialize the matrix.
        shape: A tuple specifying the shape of the matrix (rows, cols).
    """
    self.data = data
    self.size = shape[0] * shape[1]
    self.shape = shape
    self.strides = (shape[1], 1)  # Row-major order
    if len(data) != shape[0] * shape[1]:
        raise "Data length does not match the specified shape."