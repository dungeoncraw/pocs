.section __TEXT, __text
.globl _start
.p2align 2

_start:
    mov     w0, #10
    mov     w1, #20         
    cmp     w0, w1         // compare w0 and w1
    b.gt   greater_label   // branch if w0 > w1

greater_label:
    // Code for w0 > w1
    mov     x0, #1
    adrp    x1, msg_less@PAGE
    add     x1, x1, msg_less@PAGEOFF
    mov     x2, #20
    mov     x16, #4
    svc     #0x80
    b       end_label

less_label:
    mov     x0, #1
    adrp    x1, msg_greater@PAGE
    add     x1, x1, msg_greater@PAGEOFF
    mov     x2, #15
    mov     x16, #4
    svc     #0x80

end_label:
    // exit(0)
    mov     x0, #0
    mov     x16, #1
    svc     #0x80

.section __DATA,__data
.p2align 2
msg_less:
    .ascii  "w0 is less than w1\n"
msg_greater:
    .ascii  "w0 is greater than w1\n"