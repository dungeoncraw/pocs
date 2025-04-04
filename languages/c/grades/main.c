#include <stdio.h>

int main(void) {
    int score = 85;
    char grade;
    if (score >= 90) {
        grade = 'A';
    } else if (score >=80) {
        grade = 'B';
    } else if (score >= 70) {
        grade = 'C';
    } else if (score >= 60) {
        grade = 'D';
    } else {
        grade = 'F';
    }
    printf("Your grade is %c\n", grade);
    return 0;
}