;Function: multiply_and_add(x0, x1, x2) -> x0
; Does: (x0 * x1) + x2
; Arguments: x0, x1, x2
; Returns: x0

.section __TEXT,__text
.globl _start
.p2align 2

multiply_and_add:
    sub    sp, sp, #32
    stp    x29, x30, [sp, #16]
    stp    x19, x20, [sp]
    mov    x29, sp

    mov    x19, x0
    mov    x20, x1
    mov    x21, x2

    mov    x0, x19
    mov    x1, x20
    bl     multiply
    
    add    x0, x0, x21

    ldp    x19, x20, [sp]
    ldp    x29, x30, [sp, #16]
    add    sp, sp, #32
    ret

multiply:
    mul    x0, x0, x1
    ret

_start:
    sub    sp, sp, #16
    stp    x29, x30, [sp]
    mov    x29, sp

    mov    x0, #6
    mov    x1, #7
    mov    x2, #8
    bl     multiply_and_add
    // x0 = 50
    
    // Save result
    mov    x19, x0
    
    // Print "Result: "
    mov    x0, #1                  // stdout
    adrp   x1, msg@PAGE
    add    x1, x1, msg@PAGEOFF
    mov    x2, #8                  // length of "Result: "
    mov    x16, #4                 // write syscall
    svc    #0x80
    
    // Convert x19 to string and print it
    mov    x0, x19                 // x0 = 50
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    
    // Convert to ASCII: 50 -> "50"
    mov    x2, #10
    udiv   x3, x0, x2              // x3 = 50 / 10 = 5
    msub   x4, x3, x2, x0          // x4 = 50 - (5*10) = 0
    
    add    x3, x3, #48             // '5'
    add    x4, x4, #48             // '0'
    
    strb   w3, [x1]
    strb   w4, [x1, #1]
    
    // Print result
    mov    x0, #1                  // stdout
    mov    x2, #2                  // length
    mov    x16, #4                 // write syscall
    svc    #0x80
    
    // Print newline
    mov    x0, #1
    adrp   x1, newline@PAGE
    add    x1, x1, newline@PAGEOFF
    mov    x2, #1
    mov    x16, #4
    svc    #0x80

    // exit(0)
    mov    x0, #0
    mov    x16, #1
    svc    #0x80

    ldp    x29, x30, [sp]
    add    sp, sp, #16
    ret

.section __DATA,__data
msg:
    .ascii "Result: "
newline:
    .ascii "\n"
buffer:
    .space 20