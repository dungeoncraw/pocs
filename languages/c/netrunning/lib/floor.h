#ifndef CBR_FLOOR_H
#define CBR_FLOOR_H

typedef struct Floor {
  int level;
  int cost;
  Program program[];
} Floor;

#endif