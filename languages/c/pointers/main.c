#include <stdio.h>
#include <stdlib.h>

void simple_pointer() {
    int num = 10;
    int *ptr; // new pointer
    ptr = &num; // pass the address to ptr
    printf("The address of num: %p\n", ptr); // print the address of num
    printf("The value of num: %d\n", num); // print the value of num
    printf("Value pointed by ptr: %d\n", *ptr); // print the value pointed by ptr

    *ptr = 20; // change the value pointed by ptr
    printf("The value of num: %d\n", num); // print the value of num
    printf("Value pointed by ptr: %d\n", *ptr); // print the value pointed by ptr, now 20
}

void access_array() {
    int arr[5] = {100, 200, 300, 400, 500};
    int *ptr2 = arr;
    printf("Array of elements using pointer arithmetic: \n");
    for (int i = 0; i < 5; i++) {
        printf("Element at address %p: %d\n", ptr2, *ptr2);
        ptr2++; // increment the pointer (ptr2 = ptr2 + 1
    }
}

int sum_array(int *arr, int size) {
    int sum = 0;
    for (int i = 0; i < size; i++) {
        sum += *(arr + i);
    }
    return sum;
}

void sum_example() {
    int numbers[5] = {1, 2, 3, 4, 5};
    // this gets the whole array to calculate
    int arr_size = sizeof(numbers) / sizeof(numbers[0]);
    // passing name of the array is the same as a pointer to array
    int total_sum = sum_array(numbers, arr_size);
    printf("The sum of the array elements is: %d\n", total_sum);
}

void swap(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

void swap_example() {
    int x = 10, y= 20;
    printf("Before swap: x = %d, y = %d\n", x, y);
    swap(&x, &y);
    printf("After swap: x = %d, y = %d\n", x, y);
}

int alloc_memory() {
    int n;
    int *dynamic_arr;
    printf("Enter the size of the array:\n");
    scanf("%d", &n);

    dynamic_arr = (int *) malloc(n * sizeof(int));

    if (dynamic_arr == NULL) {
        fprintf(stderr, "Error allocating memory\n");
        return 1;
    }
    printf("Enter %d elements for the array:\n", n);
    for (int i = 0; i < n; i++) {
        scanf("%d", &dynamic_arr[i]);
    }
    printf("The elements of the array are:\n");
    for (int i=0; i<n;i++) {
        printf("%d\n", dynamic_arr[i]);
    }
    printf("\n");
    free(dynamic_arr);
    return 0;
}

int main(void) {
    simple_pointer();
    access_array();
    sum_example();
    swap_example();
    alloc_memory();
    return 0;
}
