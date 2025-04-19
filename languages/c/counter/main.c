#include <stdio.h>
#include "counter.h"

int main(void) {
    Counter* myCounter = createCounter();
    incrementCounter(myCounter);
    incrementCounter(myCounter);
    printf("Counter value: %d\n", getCounterValue(myCounter));
    destroyCounter(myCounter);
    return 0;
}