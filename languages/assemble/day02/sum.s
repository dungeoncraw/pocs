.section __TEXT,__text
.globl _start
.p2align 2

_start:
    // Calculate 25 + 30
    mov     x0, #25                 // x0 = 25
    mov     x1, #30                 // x1 = 30
    add     x0, x0, x1              // x0 = 25 + 30 = 55
    
    // Convert 55 to ASCII string
    mov     x1, #10                 // x1 = 10 (divisor)
    udiv    x2, x0, x1              // x2 = 55 / 10 = 5 (first digit)
    msub    x3, x2, x1, x0          // x3 = 55 - (5 * 10) = 5 (second digit)
    
    // Store digits in memory
    add     x2, x2, #48             // convert 5 to '5' (ASCII)
    add     x3, x3, #48             // convert 5 to '5' (ASCII)
    
    adrp    x4, buffer@PAGE
    add     x4, x4, buffer@PAGEOFF
    strb    w2, [x4]                // store first '5'
    strb    w3, [x4, #1]            // store second '5'
    
    // write(1, buffer, 2)
    mov     x0, #1                  // stdout
    mov     x1, x4                  // buffer address
    mov     x2, #2                  // size
    mov     x16, #4                 // syscall: write
    svc     #0x80

    // exit(0)
    mov     x0, #0
    mov     x16, #1
    svc     #0x80

.section __DATA,__data
.p2align 2
buffer:
    .space  16                       // buffer for string result