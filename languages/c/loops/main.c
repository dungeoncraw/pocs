#include <stdio.h>

int main(void) {
    int sum = 0;
    for (int i = 0; i <= 10000; i++) {
        sum += i;
    }
    printf("Sum of 10000 is %d\n", sum);

    int number;
    do {
        printf("Enter a positive number\n");
        scanf("%d", &number);
        if (number < 0) {
            printf("Please enter a positive number\n");
        }
    } while (number < 0);

    printf("You entered %d\n", number);

    return 0;
}