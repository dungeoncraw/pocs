#include <stdio.h>
#include <math.h>

float calculate_circle_area(float radius) {
    const float PI = 3.14159;
    return PI * radius * radius;
}
void increment(int num) {
    num++;
    printf("inside func: num is now %d\n", num);
}

unsigned long long int factorial(int num) {
    if (num ==0) {
        return 1;
    }
    return num * factorial(num - 1);
}

int main() {
    const float radius = 5.0;
    const float area = calculate_circle_area(radius);
    printf("Area of circle with radius %f is %f\n", radius, area);

    float side1 = 3.0;
    float side2 = 4.0;
    float hypotenuse = sqrt(pow(side1, 2) + pow(side2, 2));
    printf("Hypotenuse of triangle with sides %f and %f is %f\n", side1, side2, hypotenuse);

    int value = 10;
    printf("Before function: %d\n", value);
    // parameter passed by value, no side effect
    increment(value);
    printf("After function: %d\n", value);

    int num = 5;
    unsigned long long int fact = factorial(num);
    printf("Factorial of %d is %llu\n", num, fact);

    return 0;
}