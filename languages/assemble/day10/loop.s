.section __TEXT,__text
.globl _start
.p2align 2

_start:
    sub    sp, sp, #16
    stp    x29, x30, [sp]
    mov    x29, sp

    mov    w19, #1
    
count_up:
    cmp    w19, #10
    b.gt   count_down
    
    ; Print counter
    mov    w0, w19
    bl     print_number
    
    add    w19, w19, #1
    b      count_up

count_down:
    mov    w20, #10

loop_cbz:
    sub    w20, w20, #1
    cbz    w20, loop_cbnz
    
    mov    w0, w20
    bl     print_number
    
    b      loop_cbz

loop_cbnz:
    mov    w21, #1

count_with_cbnz:
    mov    w0, w21
    bl     print_number
    
    add    w21, w21, #1
    cmp    w21, #6
    b.lt   count_with_cbnz
    
    ; Print final message
    adrp   x0, done_msg@PAGE
    add    x0, x0, done_msg@PAGEOFF
    bl     print_string
    
    ; exit(0)
    mov    x0, #0
    mov    x16, #1
    svc    #0x80

print_number:
    mov    w1, w0
    mov    w2, #10
    udiv   w3, w1, w2
    msub   w4, w3, w2, w1
    
    add    w3, w3, #48
    add    w4, w4, #48
    
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    strb   w3, [x1]
    strb   w4, [x1, #1]
    
    mov    x0, #1
    mov    x2, #2
    mov    x16, #4
    svc    #0x80
    
    adrp   x1, space@PAGE
    add    x1, x1, space@PAGEOFF
    mov    x0, #1
    mov    x2, #1
    mov    x16, #4
    svc    #0x80
    
    ret

print_string:
    mov    x1, x0
    mov    x0, #1
    mov    x2, #9
    mov    x16, #4
    svc    #0x80
    ret

.section __DATA,__data
space:
    .ascii " "
done_msg:
    .ascii "\nDone!\n"
buffer:
    .space 20