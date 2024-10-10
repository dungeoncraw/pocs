//
// Now that we've learned about arrays, we can talk about strings.
//
// We've already seen Zig string literals: "Hello world.\n"
//
// Zig stores strings as arrays of bytes.
//
//     const foo = "Hello";
//
// Is almost* the same as:
//
//     const foo = [_]u8{ 'H', 'e', 'l', 'l', 'o' };
//
// (* We'll see what Zig strings REALLY are in Exercise 77.)
//
// Notice how individual characters use single quotes ('H') and
// strings use double quotes ("H"). These are not interchangeable!
//

const std = @import("std");

pub fn main() void {
    const ziggy = "stardust";
    // (Problem 1)
    // Use array square bracket syntax to get the letter 'd' from
    // the string "stardust" above.
    const d: u8 = ziggy[4];
    // (Problem 2)
    // Use the array repeat '**' operator to make "ha ha ha ".
    const laugh = "ha " ** 3;
    // (Problem 3)
    // Use the array concatenation '++' operator to make "Major Tom".
    // (You'll need to add a space as well!)
    const major = "Major";
    const tom = "Tom";
    // concat using ++ is funny
    const major_tom = major ++ tom;
    // That's all the problems. Let's see our results:
    std.debug.print("d={u} {s}{s}\n", .{ d, laugh, major_tom });
}
