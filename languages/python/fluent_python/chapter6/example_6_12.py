class HauntedBus:
    """A bus model haunted by ghost passengers"""
    def __init__(self, passengers=[]):
        self.passengers = passengers
    def pick(self, name):
        self.passengers.append(name)
    def drop(self, name):
        self.passengers.remove(name)


if __name__ == "__main__":
    bus1 = HauntedBus(['Alice', 'Bill'])
    print(bus1.passengers)
    bus1.pick('Charlie')
    bus1.drop('Alice')
    print(bus1.passengers)

    bus2 = HauntedBus()
    bus2.pick('Charlie')
    print(bus2.passengers)

    bus3 = HauntedBus()
    print(bus3.passengers)
    bus3.pick('Carrie')
    bus3.pick('Dave')
    print(bus2.passengers)
    print(bus3.passengers)
    print(bus2.passengers is bus3.passengers)
    print(bus1.passengers)