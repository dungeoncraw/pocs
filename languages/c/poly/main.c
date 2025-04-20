#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef struct Shape {
    void (*draw)(struct Shape*);
} Shape;

typedef struct Circle {
    Shape base;
    float radius;
} Circle;

typedef struct Rectangle {
    Shape base;
    float side;
} Square;

void drawCircle(Shape* shape) {
    Circle* circle = (Circle*)shape; // Cast Shape pointer to Circle Pointer
    printf("Drawing circle with radius %f\n", circle->radius);
}

void drawSquare(Shape* shape) {
    Square* square = (Square*)shape; // Cast Shape pointer to Square Pointer
    printf("Drawing square with side %f\n", square->side);
}

Circle* createCircle(float radius) {
    Circle* circle = (Circle*)malloc(sizeof(Circle));
    if (!circle) {
        printf("Failed to allocate memory for circle\n");
        exit(1);
    }
    circle->base.draw = drawCircle;
    circle->radius = radius;
    return circle;
}

Square* createSquare(float side) {
    Square* s = (Square*)malloc(sizeof(Square));
    if (!s) {
        printf("Failed to allocate memory for square\n");
        exit(1);
    }
    s->base.draw = drawSquare;
    s->side = side;
    return s;
}

void example_polymorphism() {
    Shape* shapes[2];
    shapes[0] = (Shape*)createCircle(10.2f);
    shapes[1] = (Shape*)createSquare(10.2f);
    for (int i = 0; i < 2; i++) {
        shapes[i]->draw(shapes[i]);
    }
    free(shapes[0]);
    free(shapes[1]);
}

int main(void) {
    example_polymorphism();
    return 0;
}