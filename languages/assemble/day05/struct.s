.section __TEXT,__text
.globl _start
.p2align 2

_start:
    adrp    x0, counter@PAGE
    add     x0, x0, counter@PAGEOFF
    ldr     w1, [x0]
    mov     x0, #0
    mov     x16, #1
    svc     #0x80

.section __DATA,__data
.p2align 2

counter:
    .word   42
message:
    .ascii  "Counter value printed.\n"

.section __DATA,__bss
.p2align 3

large_buffer:
    .space  4096