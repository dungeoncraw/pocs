.section __TEXT,__text
.globl _start
.p2align 2

_start:

    // ((15 + 7) * 2) - 4
    mov   x0, #15
    mov   x1, #7
    add   x0, x0, x1
    mov   x1, #2
    mul   x0, x0, x1
    mov   x1, #4
    sub   x0, x0, x1

    // exit(0)
    mov    x0, #0
    mov    x16, #1                // syscall: exit
    svc    #0x80

.section __DATA,__data
.p2align 2
buffer:
    .space  16