.section __TEXT,__text
.globl _start
.p2align 2

_start:
    // print "Hello, World!\n"
    mov     x0, #1               // stdout fd
    adrp    x1, message@PAGE
    add     x1, x1, message@PAGEOFF
    mov     x2, #13              // length of "Hello, World!\n"
    mov     x16, #4              // syscall: write
    svc     #0x80

    // exit(0)
    mov     x0, #0
    mov     x16, #1              // syscall: exit
    svc     #0x80

.section __DATA,__data
.p2align 2
message:
    .ascii  "Hello, World!\n"