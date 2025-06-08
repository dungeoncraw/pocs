#include <stdlib.h>
#include "program.h"

#include <time.h>

int calculate_cost(Program *program) {
    return program->cost;
}

int play_attack(Program *program) {
    // clear seed
    srand(time(NULL));
    return rand() % 10 + 1;
}