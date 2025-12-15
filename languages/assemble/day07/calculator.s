.section __TEXT,__text
.globl _start
.p2align 2

_start:
    // Define two numbers
    mov    w0, #25          // First number: 25
    mov    w1, #5           // Second number: 5
    
    // Addition: 25 + 5 = 30
    add    w2, w0, w1
    mov    w9, w2           // Save result in w9
    bl     print_result_add
    
    // Subtraction: 25 - 5 = 20
    sub    w3, w0, w1
    mov    w9, w3           // Save result in w9
    bl     print_result_sub
    
    // Multiplication: 25 * 5 = 125
    mul    w4, w0, w1
    mov    w9, w4           // Save result in w9
    bl     print_result_mul
    
    // Division: 25 / 5 = 5
    udiv   w5, w0, w1
    mov    w9, w5           // Save result in w9
    bl     print_result_div
    
    // exit(0)
    mov    x0, #0
    mov    x16, #1
    svc    #0x80

// Print "25 + 5 = " and result
print_result_add:
    mov    x0, #1
    adrp   x1, msg_add@PAGE
    add    x1, x1, msg_add@PAGEOFF
    mov    x2, #9           // "25 + 5 = "
    mov    x16, #4
    svc    #0x80
    
    // Print the result (w9)
    mov    w0, w9
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    mov    x4, x1           // Save buffer address
    
    // Convert result to ASCII
    mov    w8, #10
    udiv   w6, w0, w8       // First digit
    msub   w7, w6, w8, w0   // Second digit
    add    w6, w6, #48
    add    w7, w7, #48
    strb   w6, [x4]
    strb   w7, [x4, #1]
    
    mov    x0, #1
    mov    x1, x4
    mov    x2, #2
    mov    x16, #4
    svc    #0x80
    
    // Print newline
    mov    x0, #1
    adrp   x1, newline@PAGE
    add    x1, x1, newline@PAGEOFF
    mov    x2, #1
    mov    x16, #4
    svc    #0x80
    
    ret

// Print "25 - 5 = " and result
print_result_sub:
    mov    x0, #1
    adrp   x1, msg_sub@PAGE
    add    x1, x1, msg_sub@PAGEOFF
    mov    x2, #9           // "25 - 5 = "
    mov    x16, #4
    svc    #0x80
    
    mov    w0, w9
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    mov    x4, x1
    
    mov    w8, #10
    udiv   w6, w0, w8
    msub   w7, w6, w8, w0
    add    w6, w6, #48
    add    w7, w7, #48
    strb   w6, [x4]
    strb   w7, [x4, #1]
    
    mov    x0, #1
    mov    x1, x4
    mov    x2, #2
    mov    x16, #4
    svc    #0x80
    
    mov    x0, #1
    adrp   x1, newline@PAGE
    add    x1, x1, newline@PAGEOFF
    mov    x2, #1
    mov    x16, #4
    svc    #0x80
    
    ret

// Print "25 * 5 = " and result
print_result_mul:
    mov    x0, #1
    adrp   x1, msg_mul@PAGE
    add    x1, x1, msg_mul@PAGEOFF
    mov    x2, #9           // "25 * 5 = "
    mov    x16, #4
    svc    #0x80
    
    mov    w0, w9
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    mov    x4, x1
    
    mov    w8, #10
    udiv   w6, w0, w8
    msub   w7, w6, w8, w0
    add    w6, w6, #48
    add    w7, w7, #48
    strb   w6, [x4]
    strb   w7, [x4, #1]
    
    mov    x0, #1
    mov    x1, x4
    mov    x2, #2
    mov    x16, #4
    svc    #0x80
    
    mov    x0, #1
    adrp   x1, newline@PAGE
    add    x1, x1, newline@PAGEOFF
    mov    x2, #1
    mov    x16, #4
    svc    #0x80
    
    ret

// Print "25 / 5 = " and result
print_result_div:
    mov    x0, #1
    adrp   x1, msg_div@PAGE
    add    x1, x1, msg_div@PAGEOFF
    mov    x2, #9           // "25 / 5 = "
    mov    x16, #4
    svc    #0x80
    
    mov    w0, w9
    adrp   x1, buffer@PAGE
    add    x1, x1, buffer@PAGEOFF
    mov    x4, x1
    
    // Single digit result (5)
    add    w0, w0, #48
    strb   w0, [x4]
    
    mov    x0, #1
    mov    x1, x4
    mov    x2, #1           // Single digit
    mov    x16, #4
    svc    #0x80
    
    mov    x0, #1
    adrp   x1, newline@PAGE
    add    x1, x1, newline@PAGEOFF
    mov    x2, #1
    mov    x16, #4
    svc    #0x80
    
    ret

.section __DATA,__data
.p2align 2
msg_add:
    .ascii  "25 + 5 = "
msg_sub:
    .ascii  "25 - 5 = "
msg_mul:
    .ascii  "25 * 5 = "
msg_div:
    .ascii  "25 / 5 = "
newline:
    .ascii  "\n"
buffer:
    .space  16