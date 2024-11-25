//
// Behold the 'for' loop! For loops let you execute code for each
// element of an array:
//
//     for (items) |item| {
//
//         // Do something with item
//
//     }
//

const std = @import("std");

pub fn main() void {
    const story = [_]u8{ 'h', 'h', 's', 'n', 'h' };
    std.debug.print("A dramatic story: ", .{});
    for (story) |scene| {
        if (scene == 'h') std.debug.print(":-)\n", .{});
        if (scene == 's') std.debug.print(":-(\n", .{});
        if (scene == 'n') std.debug.print(":-|\n", .{});
    }

    std.debug.print("The end.\n", .{});
}
