# Netrunning - Cyberpunk Red - Net Architecture

Goal: create net architecture floors based on Netrunning decks

Must return some information like if the encounter is easy/lethal and also the amount of Eurodollar needed for the net 



### Temporary information

Folder structure

1. src = main executable program
2. lib = library functions for reuse
3. include = public header included by executable—separation of interface and implementation
4. test = unit tests

### Data structure - Initial step

* **Netrunner**
  - Name
  - Level
  - Deck type—poor (five programs), good (seven programs), excellent (nine programs)
  > Deck can also have hardware slots, this would be added in the future
* **Program**
    - Name
    - Description
    - Type = Program/Black Ice/Demon/Floor
    - Stats
        - Attack - Program/Black Ice/Demon
        - Defense - Program/Black Ice/Demon
        - Rez - Program/Black Ice/Demon
        - DV (Difficult Value)—list of 4 DV predefined—Floor
* **Floor**
    - Name
    - Architecture (List of Programs/Black Ice/Demon/Floor)
    - EB cost

### Designs

![Home](./netrunning_1.svg "Home")
![Net Architecture](./netrunning_2.svg "Net Architecture")

> main article https://rtalsoriangames.com/wp-content/uploads/2021/10/RTG-CPR-NetrunningDeck-Instructionsv1.1.pdf



### Building

`gcc $(pkg-config --cflags gtk4) -o netrunning ./src/main.c $(pkg-config --libs gtk4)`

`./netrunning`


### Tests

Using cunit framework https://cunity.gitlab.io/cunit/