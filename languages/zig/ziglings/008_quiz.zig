//
// Quiz time! Let's see if you can fix this whole program.
//
// You'll have to think about this one a bit.
//
// Let the compiler tell you what's wrong.
//
// Start at the top.
//

const std = @import("std");

pub fn main() void {
    const letters = "YZhifg";
    var x: usize = 1;
    var lang: [3]u8 = undefined;
    lang[0] = letters[x];

    x = 3;
    lang[1] = letters[x];

    x = 5;

    lang[2] = letters[x];

    // We want to "Program in Zig!" of course:
    std.debug.print("Program in {s}!\n", .{lang});
}
