#ifndef CBR_PROGRAM_H
#define CBR_PROGRAM_H

enum CBR_PROGRAM_TYPE {
    PROGRAM,
    BLACK_ICE,
    DEMON,
};

typedef struct Stats {
    int attack, defense, rez, dv;
} Stats;

typedef struct Program {
    char name[16];
    char description[220];
    enum CBR_PROGRAM_TYPE type[];
    Stats stats[];
    int cost;
} Program;


int calculate_cost(Program *program);
int play_attack(Program *program);
void set_damage(Program *program, int damage);

#endif