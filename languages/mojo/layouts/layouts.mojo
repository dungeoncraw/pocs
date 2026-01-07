from layout import Layout, LayoutTensor, print_layout
from layout.layout import blocked_product

fn main():
    # layouts must be comptime to use inside LayoutTensor
    # tile: 10x10, tiler: 2x5 -> tiled_layout: 20x50
    comptime tile  = Layout.col_major(10, 10)
    comptime tiler = Layout.col_major(2, 5)
    comptime tiled_layout = blocked_product(tile, tiler)

    # Define tile dimensions as constants
    comptime tile_rows = 10
    comptime tile_cols = 10
    comptime grid_rows = 2
    comptime grid_cols = 5

    print("Tiled layout (20x50):")
    print_layout(tiled_layout)
    # Allocate storage for 20x50 = 1000 elements
    comptime buf_size = grid_rows * grid_cols * tile_rows * tile_cols
    # InlineArray created fixed size array on stack
    # uninitialized allocates but does not fill the array, so can have garbage values
    var storage = InlineArray[Float32, buf_size](uninitialized=True)

    var X = LayoutTensor[DType.float32, tiled_layout](storage)

    # Fill the tensor with values 0, 1, 2, ..., 999 for demonstration
    for i in range(buf_size):
        storage[i] = Float32(i)

    comptime out_layout = Layout.row_major(grid_rows, grid_cols)
    var out_storage = InlineArray[Float32, grid_rows * grid_cols](fill=0)
    var tile_sums = LayoutTensor[DType.float32, out_layout](out_storage)

    for rb in range(grid_rows):
        for cb in range(grid_cols):
            var s: Float32 = 0.0
            # tile_rows * tile_cols = 10 * 10 = 100 elements per tile
            for r_in in range(tile_rows):
                for c_in in range(tile_cols):
                    # r_in = row inside tile
                    # c_in = col inside tile
                    # rb = row block index
                    # cb = col block index
                    s += X[r_in, rb, c_in, cb][0]
            tile_sums[rb, cb] = s

    print("\nTile sums (2x5):")
    for rb in range(grid_rows):
        for cb in range(grid_cols):
            print("tile_sums[", rb, ",", cb, "] = ", tile_sums[rb, cb][0])