#include <stdio.h>

float calculate_average(int arr[], int size) {
    int sum = 0;
    for (int i =0; i < size; i++) {
        sum  += arr[i];
    }
    return (float) sum / size;
}

int main(void) {
    const int arraySize = 5;
    // cannot be student_scores[arraySize] because the value need to be defined beforehand
    // but once arraySize is a const, this works.
    int student_scores[arraySize] = {85, 92, 78, 95, 88};
    int sum  = 0;
    float average;

    for (int i = 0; i < arraySize ; i++) {
        sum += student_scores[i];
    }
    average = (float) sum / arraySize;
    printf("Average student score is: %.2f", average);

    int data[10];
    for (int i = 0; i < 10; i++) {
        data[i] = i*2;
    }
    printf("Initialized the array");
    for (int i = 0; i < 10; i++) {
        printf("%d\n", data[i]);
    }
    printf("Done\n");

    int scores[] = {75, 80, 90, 85};
    // sizeof get the size in bytes of the scores array, so to know how many are there
    // just divide by the size of the first element
    int num_scores = sizeof(scores) / sizeof(scores[0]);
    float average_scores = calculate_average(scores, num_scores);
    printf("Average score is: %.2f\n", average_scores);


    int matrix[2][3] = {{1, 2, 3}, {4, 5, 6}};
    int sumMatrix = 0;
    for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 3; j++) {
            sumMatrix += matrix[i][j];
            printf("matrix[%d][%d] = %d\n", i, j, sumMatrix);
        }
        printf("new line for each row\n");
    }
    printf("Sum of all elements: %d\n", sumMatrix);
    return 0;
}