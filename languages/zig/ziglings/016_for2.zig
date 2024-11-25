//
// For loops also let you use the "index" of the iteration, a number
// that counts up with each iteration. To access the index of iteration,
// specify a second condition as well as a second capture value.
//
//     for (items, 0..) |item, index| {
//
//         // Do something with item and index
//
//     }
//
// You can name "item" and "index" anything you want. "i" is a popular
// shortening of "index". The item name is often the singular form of
// the items you're looping through.
//
const std = @import("std");

pub fn main() void {
    const bits = [_]u8{ 1, 0, 1, 1 };
    var value: u32 = 0;
    for (bits, 0..) |bit, i| {
        const i_u32: u32 = @intCast(i);
        const place_value = std.math.pow(u32, 2, i_u32);
        value += place_value * bit;
    }
    std.debug.print("The value of bits '1101': {}.\n", .{value});
}
