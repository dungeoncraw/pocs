#include <stdio.h>

int main(void) {
    int a = 5;
    int b = 5;

    int prefix_increment = ++a;
    int postfix_increment = b++;

    // must be 6
    printf("prefix_increment of a: %d = %d\n", a, prefix_increment);
    // must be 5
    printf("postfix_increment of b: %d = %d\n", b, postfix_increment);

    int prefix_decrement = --a;
    int postfix_decrement = b--;
    printf("prefix_decrement of a: %d = %d\n", a, prefix_decrement);
    printf("postfix_decrement of b: %d = %d\n", b, postfix_decrement);

    return 0;
}