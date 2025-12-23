.section __TEXT,__text
.globl _start
.p2align 2

_start:
    mov     w19, #0          // counter = 0
    mov     w20, #10         // limit = 10

loop:
    cmp     w19, w20
    b.ge    exit             // if counter >= 10, exit

    // Print counter (simplified, just the digit)
    mov     w0, w19
    add     w0, w0, #48      // convert to ASCII
    
    adrp    x1, buffer@PAGE
    add     x1, x1, buffer@PAGEOFF
    strb    w0, [x1]
    
    mov     x0, #1           // stdout
    mov     x2, #1           // 1 byte
    mov     x16, #4          // write
    svc     #0x80

    // Print newline
    adrp    x1, newline@PAGE
    add     x1, x1, newline@PAGEOFF
    mov     x0, #1
    mov     x2, #1
    mov     x16, #4
    svc     #0x80

    add     w19, w19, #1     // counter++
    b       loop

exit:
    mov     x0, #0
    mov     x16, #1
    svc     #0x80

.section __DATA,__data
.p2align 2
newline:
    .ascii  "\n"
buffer:
    .space  1
