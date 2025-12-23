.section __TEXT,__text
.globl _start
.p2align 2

// Recursive Factorial Function
// Input: x0 (n)
// Output: x0 (n!)
factorial:
    sub     sp, sp, #16     // Allocate stack frame
    str     lr, [sp, #8]    // Save link register
    str     x0, [sp]        // Save current n

    cmp     x0, #1
    b.le    base_case       // If n <= 1, return 1

    sub     x0, x0, #1      // n - 1
    bl      factorial       // factorial(n - 1)
    
    ldr     x1, [sp]        // Load original n
    mul     x0, x0, x1      // n * factorial(n - 1)
    b       fact_return

base_case:
    mov     x0, #1          // return 1

fact_return:
    ldr     lr, [sp, #8]    // Restore link register
    add     sp, sp, #16     // Deallocate stack frame
    ret

_start:
    mov     x0, #5          // Calculate factorial of 5 (120)
    bl      factorial
    
    // Result is in x0. Let's convert it to string and print it.
    // 120 has 3 digits.
    mov     x19, x0         // Save result in x19
    
    // Print prefix
    mov     x0, #1
    adrp    x1, msg@PAGE
    add     x1, x1, msg@PAGEOFF
    mov     x2, #15
    mov     x16, #4
    svc     #0x80

    // Convert x19 to string
    // x19 = 120
    mov     x0, x19
    adrp    x1, buffer@PAGE
    add     x1, x1, buffer@PAGEOFF
    add     x1, x1, #10     // Start from end of buffer
    mov     x2, #0          // length counter

convert_loop:
    mov     x3, #10
    udiv    x4, x0, x3      // x4 = x0 / 10
    msub    x5, x4, x3, x0  // x5 = x0 - (x4 * 10) -> remainder
    add     x5, x5, #48     // convert to ASCII
    sub     x1, x1, #1
    strb    w5, [x1]
    add     x2, x2, #1
    mov     x0, x4
    cmp     x0, #0
    b.ne    convert_loop

    // Print the number
    mov     x0, #1          // stdout
    // x1 already points to the start of the string
    // x2 already has the length
    mov     x16, #4
    svc     #0x80

    // Print newline
    mov     x0, #1
    adrp    x1, newline@PAGE
    add     x1, x1, newline@PAGEOFF
    mov     x2, #1
    mov     x16, #4
    svc     #0x80

    // exit(0)
    mov     x0, #0
    mov     x16, #1
    svc     #0x80

.section __DATA,__data
.p2align 2
msg:
    .ascii  "Factorial(5) = "
newline:
    .ascii  "\n"
buffer:
    .space  16
