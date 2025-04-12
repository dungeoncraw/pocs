#include <stdio.h>

void simple_open_file_example() {
    FILE *fptr;
    fptr = fopen("test.txt", "w");
    if (fptr == NULL) {
        printf("Error opening file");
        return;
    }
    printf("File opened successfully for writing\n");
    fclose(fptr);
    printf("File closed successfully\n");
}

int main(void) {
    simple_open_file_example();
    return 0;
}