#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

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

void write_to_file_example() {
    FILE *fptr_write, *fptr_read;
    char filename[] =  "datafile.txt";
    char data_to_write[] = "This is some text\nand this is a new line";
    char buffer[100];
    int number = 123;
    float value = 3.14;
    // open file to write
    int random = time(NULL) % 2;
    printf("Random number: %d\n", random);
    if (random == 0) {
        fptr_write = fopen(filename, "w");
    } else {
        // this will force the crash so can check the errno and strerror(errno)
        char unknown_filename[] =  "datafile2.txt";
        fptr_write = fopen(unknown_filename, "r");
    }

    if (fptr_write == NULL) {
        perror("Error opening file");
        printf("errno value: %d\n", errno);
        printf("Error message: %s\n", strerror(errno));
        exit(1);
    }
    // single line
    fprintf(fptr_write, "Number: %d, value: %.2f\n", number, value);
    fputs(data_to_write, fptr_write);
    fclose(fptr_write);
    // open file to read
    fptr_read = fopen(filename, "r");
    if (fptr_read == NULL) {
        perror("Error opening file");
        exit(1);
    }

    printf("File opened successfully for reading\n");
    // this check if string match in file content
    while (fscanf(fptr_read, "Number: %d, value: %f\n", &number, &value) == 2) {
        printf("Number: %d, value: %.2f\n", number, value);
    }

    while (fgets(buffer, 100, fptr_read) != NULL) {
        printf("%s", buffer);
    }
    fclose(fptr_read);
    printf("File closed successfully\n");
}

int main(void) {
    simple_open_file_example();
    write_to_file_example();
    return 0;
}