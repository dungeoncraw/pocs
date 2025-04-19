#ifndef COUNTER_H
#define COUNTER_H

typedef struct Counter Counter;

Counter* createCounter();
void incrementCounter(Counter* c);
int getCounterValue(Counter* c);
void destroyCounter(Counter* c);

#endif