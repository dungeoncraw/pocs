#include <stdio.h>
#include <stdlib.h>

// define a STACK ADT
// typedef is to create alias
typedef struct {
    int *items;
    int top;
    int capacity;
} Stack;

Stack* createStack(int capacity) {
    Stack* s = (Stack*) malloc(sizeof(Stack));
    if (!s) {
        fprintf(stderr, "Error: could not allocate memory\n");
        exit(1);
    }
    s -> capacity = capacity;
    s -> top = -1;
    s -> items = (int*) malloc(s->capacity * sizeof(int));
    if (!s->items) {
        fprintf(stderr, "Error: could not allocate memory\n");
        exit(1);
    }
    return s;
}
int isEmpty(Stack* s) {
    return s->top == -1;
}

int isFull(Stack* s) {
    return s -> top == s -> capacity - 1;
}

void push(Stack* s, int item) {
    if (isFull(s)) {
        fprintf(stderr, "Error: stack is full\n");
        exit(1);
    }
    s -> items[++s -> top] = item;
    printf("Pushed %d\n", item);
}

int pop(Stack* s) {
    if (isEmpty(s)) {
        fprintf(stderr, "Error: stack is empty\n");
        exit(1);
    }
    return s -> items[s -> top--]; // decrement top so unload the value
}

int peek(Stack* s) {
    if (isEmpty(s)) {
        fprintf(stderr, "Error: stack is empty\n");
        exit(1);
    }
    return s -> items[s -> top];
}
void simple_stack_example() {
    Stack* m = createStack(10);
    push(m, 1);
    push(m, 2);
    push(m, 3);
    printf("Peek: %d\n", peek(m));
    push(m, 4);
    push(m, 5);
    printf("Popped element: %d\n", pop(m));
    printf("Is stack empty? %s\n", isEmpty(m) ? "Yes" : "No");
    free(m);
}
// emulation of OOP as C is procedural language
int main(void) {
    simple_stack_example();
    return 0;
}