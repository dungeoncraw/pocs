#include <stdio.h>

int main() {
    int number;
    float price;

    printf("Enter an integer and a price:\n");
    // TODO: change to a type safety approach
    scanf("%d %f", &number, &price);

    printf("You entered: Number = %d, Price = %f\n", number, price);
    return 0;
}