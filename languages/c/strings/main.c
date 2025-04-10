#include <stdio.h>
#include <string.h>

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

int main(void) {
    str_copy_comp();
    return 0;
}