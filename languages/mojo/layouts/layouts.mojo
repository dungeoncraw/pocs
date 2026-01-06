from layout import Layout, LayoutTensor, print_layout
from layout.layout import blocked_product

fn main():
    # layouts must be comptime to use inside LayoutTensor
    comptime tile  = Layout.col_major(3, 2)
    comptime tiler = Layout.col_major(2, 5)
    comptime tiled_layout = blocked_product(tile, tiler)

    # Define tile dimensions as constants
    comptime tile_rows = 3
    comptime tile_cols = 2
    comptime grid_rows = 2
    comptime grid_cols = 5

    print("Tiled layout (6x10):")
    print_layout(tiled_layout)
    # Allocate storage for 6x10 = 60 elements
    comptime buf_size = 60
    # InlineArray created fixed size array on stack
    # uninitialized allocates but does not fill the array, so can have garbage values
    var storage = InlineArray[Float32, buf_size](uninitialized=True)

    var X = LayoutTensor[DType.float32, tiled_layout](storage)

    for i in range(buf_size):
        storage[i] = Float32(i)

    comptime out_layout = Layout.row_major(grid_rows, grid_cols)
    var out_storage = InlineArray[Float32, 10](fill=0)
    var tile_sums = LayoutTensor[DType.float32, out_layout](out_storage)

    for rb in range(grid_rows):
        for cb in range(grid_cols):
            var s: Float32 = 0.0
            for r_in in range(tile_rows):
                for c_in in range(tile_cols):
                    s += X[r_in, rb, c_in, cb][0]
            tile_sums[rb, cb] = s

    print("\nTile sums (2x5):")
    for rb in range(grid_rows):
        for cb in range(grid_cols):
            print("tile_sums[", rb, ",", cb, "] = ", tile_sums[rb, cb][0])