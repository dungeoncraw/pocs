.section __TEXT,__text
.globl _start
.p2align 2

_start:
    // write(1, msg, len)
    mov     x0, #1                  // stdout (fd = 1)
    adrp    x1, msg@PAGE            // página onde 'msg' está
    add     x1, x1, msg@PAGEOFF     // offset dentro da página
    mov     x2, #21                 // tamanho da string
    mov     x16, #4                 // syscall: write
    svc     #0x80

    // exit(0)
    mov     x0, #0                  // código de saída
    mov     x16, #1                 // syscall: exit
    svc     #0x80

    .section __DATA,__data
    .p2align 2
msg:
    .ascii  "Hello, Mac Assembly!\n"