
#include <stdio.h>

int main() {
    char command;

    printf("Enter a command(a, b, c, d)\n");
    scanf("%c", &command);

    switch (command) {
        case 'a':
            printf("Executing command A");
            break;
        case 'b':
            printf("Executing command B");
            break;
        case 'c':
            printf("Executing command C");
            break;
        case 'd':
            printf("Executing command D");
            break;
        default:
            printf("Invalid command");
            break;
    }
    return 0;
}