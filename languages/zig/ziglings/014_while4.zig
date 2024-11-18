//
// You can force a loop to exit immediately with a "break" statement:
//
//     while (condition) : (continue expression) {
//
//         if (other condition) break;
//
//     }
//
// Continue expressions do NOT execute when a while loop stops
// because of a break!
//
const std = @import("std");

pub fn main() void {
    var n: u32 = 1;
    while (true) : (n += 1) {
        if (n == 4) break;
    }
    // Result: we want n=4
    std.debug.print("n={}\n", .{n});
}
