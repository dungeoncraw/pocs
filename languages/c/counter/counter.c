#include <stdlib.h>
#include <stdio.h>
#include "counter.h"

struct Counter {
    int count;
};

Counter* createCounter() {
    Counter* counter = malloc(sizeof(Counter));
    if (!counter) {
        fprintf("Out of memory\n");
        exit(1);
    }
    counter->count = 0;
    return counter;
}

void incrementCounter(Counter* c) {
    c->count++;
}

int getCounterValue(Counter* c) {
    return c->count;
}

void destroyCounter(Counter* c) {
    free(c);
}