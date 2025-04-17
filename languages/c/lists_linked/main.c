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

struct NodeLinked {
    int data;
    struct NodeLinked *next;
    struct NodeLinked *previous;
};
// struct NodeLinked** head pass the pointer not a copy of it
void insert_at_beginning_doubly(int data, struct NodeLinked** head, struct NodeLinked** tail) {
    struct NodeLinked *newNode = malloc(sizeof(struct NodeLinked));
    if (newNode == NULL) {
        fprintf(stderr, "Memory allocation failed\n");
        exit(1);
    }
    newNode -> data = data;
    newNode -> previous = NULL;
    // getting pointer *head
    newNode -> next = *head;
    if (*head != NULL) {
        (*head) -> previous = newNode;
    } else {
        *tail = newNode;
    }
    *head = newNode;
    if (tail == NULL) {
        *tail = *head;
    }
}

void print_linked_list_forward(struct NodeLinked *head) {
    struct NodeLinked *current = head;
    printf("Linked list forward:\n");
    while (current != NULL) {
        printf("%d <->", current->data);
        current = current->next;
    }
    printf("NULL\n");
}
void double_linked_list_example() {
    struct NodeLinked* head = NULL;
    struct NodeLinked* tail = NULL;
    insert_at_beginning_doubly(10, &head, &tail);
    insert_at_beginning_doubly(50, &head, &tail);
    insert_at_beginning_doubly(30, &head, &tail);
    print_linked_list_forward(head);
}
int main(void) {
    simple_list_example();
    sorted_list_example();
    single_linked_list_example();
    double_linked_list_example();
    return 0;
}