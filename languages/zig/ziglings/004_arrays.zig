//
// Let's learn some array basics. Arrays are declared with:
//
//   var foo: [3]u32 = [3]u32{ 42, 108, 5423 };
//
// When Zig can infer the size of the array, you can use '_' for the
// size. You can also let Zig infer the type of the value so the
// declaration is much less verbose.
//
//   var foo = [_]u32{ 42, 108, 5423 };
//
// Get values of an array using array[index] notation:
//
//     const bar = foo[2]; // 5423
//
// Set values of an array using array[index] notation:
//
//     foo[2] = 16;
//
// Get the length of an array using the len property:
//
//     const length = foo.len;

const std = @import("std");
pub fn main() void {
    var some_primes = [_]u8{ 1, 3, 5, 7, 11, 13, 17, 19 };
    some_primes[0] = 2;

    const first = some_primes[0];

    const fourth = some_primes[3];

    const length = some_primes.len;

    std.debug.print("First: {}, Fourth: {}, Length: {}\n", .{ first, fourth, length });
}
