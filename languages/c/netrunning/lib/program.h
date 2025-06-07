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
    struct Stats stats[];
} Program;