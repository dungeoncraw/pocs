#include <stdio.h>
#include <string.h>
#include <ctype.h>

void str_copy_comp() {
    char message1[20] = "Hello";
    char message2[20];
    int length, comparison;

    length = strlen(message1);
    printf("Length of message1: %d\n", length);

    strcpy(message2, message1);
    printf("message2 after strcpy: %s\n", message2);

    comparison = strcmp(message1, message2);

    if (comparison ==0) {
        printf("message1 and message2 are equal\n");
    } else {
        printf("message1 and message2 are not equal\n");
    }

    comparison = strcmp(message1, "World");
    if (comparison < 0) {
        printf("message1 comes before 'World' lexicographically\n");
    } else if (comparison > 0) {
        printf("message1 comes after 'World' lexicographically\n");
    }
}

void string_to_upper(char str[]) {
    // this is for the end of string \0
    for (int i =0; str[i] != '\0'; i++) {
        str[i] = toupper(str[i]);
    }
}

void str_upper_example() {
    char str[20] = "Hello World";
    string_to_upper(str);
    printf("str after string_to_upper: %s\n", str);
}

int main(void) {
    str_copy_comp();
    str_upper_example();
    return 0;
}