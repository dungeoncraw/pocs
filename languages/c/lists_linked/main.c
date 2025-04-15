#include <stdio.h>
#include <stdlib.h>
#define MAX_SIZE 100

int list[MAX_SIZE];
int list_size = 0;
int sorted_list[MAX_SIZE];
int sorted_list_size = 0;

void add_element(int value) {
    if (list_size >= MAX_SIZE) {
        printf("List is full\n");
        return;
    }
    list[list_size] = value;
    list_size++;
}
void print_list() {
    printf("List elements: ");
    for (int i = 0; i < list_size; i++) {
        printf("%d ", list[i]);
    }
    printf("\n");
}

void sorted_insert(int value) {
    if (sorted_list_size >= MAX_SIZE) {
        printf("List is full\n");
        return;
    }
    int i = sorted_list_size - 1;
    // shift elements
    while (i >= 0 && sorted_list[i] > value) {
        sorted_list[i+1] = sorted_list[i];
        i--;
    }
    sorted_list[i] = value;
    sorted_list_size++;
}
int binary_search(int value) {
    int low = 0, high = sorted_list_size - 1;
    while (low <= high) {
        int mid = (low + high) / 2;
        if (sorted_list[mid] == value) {
            return mid;
        } else if (sorted_list[mid] < value) {
            low = mid +1;
        } else {
            high = mid - 1;
        }
    }
    // not found
    return -1;
}

void simple_list_example() {
    add_element(1);
    add_element(2);
    add_element(3);
    add_element(4);
    add_element(5);
    add_element(6);
    add_element(7);
    add_element(8);
    add_element(19);
    print_list();
}
void sorted_list_example() {
    sorted_insert(10);
    sorted_insert(50);
    sorted_insert(20);
    sorted_insert(3);
    sorted_insert(60);

    print_list();

    int index = binary_search(3);
    if (index == -1) {
        printf("Element not found\n");
    } else {
        printf("Element found at index %d\n", index);
    }
}

struct Node {
    int data;
    struct Node *next; // pointer to next element
};

struct Node *head = NULL;

void insert_at_beginning(int value) {
    struct Node *newNode = (struct Node *)malloc(sizeof(struct Node));
    if (newNode == NULL) {
        fprintf(stderr, "Memory allocation failed\n");
        exit(1);
    }
    newNode -> data = value;
    newNode -> next = head;
    head = newNode;
}

void print_linked_list() {
    struct Node *current = head;
    printf("Linked list:\n");
    while (current != NULL) {
        printf("%d ", current->data);
        current = current-> next;
    }
    printf("NULL\n");
}

void single_linked_list_example() {
    insert_at_beginning(10);
    insert_at_beginning(50);
    insert_at_beginning(30);
    insert_at_beginning(20);
    insert_at_beginning(90);
    print_linked_list();
}
int main(void) {
    simple_list_example();
    sorted_list_example();
    single_linked_list_example();
    return 0;
}