#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Employee {
    char name[50];
    int employee_id;
    float salary;
    char department[30];
};

struct Point {
    int x;
    int y;
};
struct Rectangle {
    struct Point top_left;
    struct Point bottom_right;
};

void struct_example() {
    struct Employee emp1;
    strcpy(emp1.name, "John Nash");
    emp1.employee_id = 1001;
    emp1.salary = 100000.00;
    strcpy(emp1.department, "IT");
    printf("Name: %s\n", emp1.name);
    printf("Employee ID: %d\n", emp1.employee_id);
    printf("Salary: %f\n", emp1.salary);
    printf("Department: %s\n", emp1.department);
}

void nested_struct_example() {
    struct Rectangle rect1;
    rect1.top_left.x = 10;
    rect1.top_left.y = 20;
    rect1.bottom_right.x = 30;
    rect1.bottom_right.y = 40;

    int width = rect1.bottom_right.x - rect1.top_left.x;
    int height = rect1.bottom_right.y - rect1.top_left.y;
    int area = width * height;
    printf("Area: %d\n", area);
}

void array_struct_example() {
    struct Employee emp_arr[3];
    emp_arr[0].employee_id = 1001;
    emp_arr[0].salary = 100000.00;
    strcpy(emp_arr[0].name, "John Nash");
    strcpy(emp_arr[0].department, "IT");
    emp_arr[1].employee_id = 1002;
    emp_arr[1].salary = 200000.00;
    strcpy(emp_arr[1].name, "Jane Doe");
    strcpy(emp_arr[1].department, "Sales");
    emp_arr[2].employee_id = 1003;
    emp_arr[2].salary = 300000.00;
    strcpy(emp_arr[2].name, "Jane Doe");
    strcpy(emp_arr[2].department, "IT");

    for (int i = 0; i < 3; i++) {
        printf("Name: %s\n", emp_arr[i].name);
        printf("Employee ID: %d\n", emp_arr[i].employee_id);
        printf("Salary: %f\n", emp_arr[i].salary);
        printf("Department: %s\n", emp_arr[i].department);
        printf("\n");
    }
}
void move_point(struct Point *p, int delta_x, int delta_y) {
    // same as (*p).x
    p->x += delta_x;
    // same as (*p).y
    p->y += delta_y;
}
void mutable_struct() {
    struct Point *point_ptr = (struct Point *)malloc(sizeof(struct Point));
    if (point_ptr == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        return;
    }
    point_ptr->x = 5;
    point_ptr->y = 8;

    printf("Point before moving: (%d, %d)\n", point_ptr->x, point_ptr->y);
    move_point(point_ptr, 10, 20);
    printf("Point after moving: (%d, %d)\n", point_ptr->x, point_ptr->y);
    free(point_ptr);
}

union Value {
    int id;
    char name[30];
};

void union_example() {
    union Value val;

    val.id = 123;
    printf("ID: %d\n", val.id);
    strcpy(val.name, "John Doe");

    printf("Name: %s\n", val.name);

    printf("Value as ID (after setting name - value is completely random): %d\n", val.id);
}

int main(void) {
    struct_example();
    nested_struct_example();
    array_struct_example();
    mutable_struct();
    union_example();
    return 0;
}