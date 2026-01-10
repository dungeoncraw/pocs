# create a custom iterator implementing __iter__ and __next__ methods
import random

from fake_names import generate_fake_names


class Raffle:
    def __init__(self, participants):
        self.participants = list(participants)

    def __iter__(self):
        return self

    def __next__(self):
        if not self.participants:
            raise StopIteration
        # randit generates a random number between 0 and the length of numbers - 1
        # randit is inclusive of both 0 and the length of numbers - 1
        winner = random.randint(0, len(self.participants) - 1)
        return self.participants.pop(winner)

if __name__ == '__main__':
    # generate some fake names
    participants = generate_fake_names(20)
    raffle = Raffle(participants)

    first = next(raffle)
    second = next(raffle)
    third = next(raffle)

    print(f"Gold: {first}, Silver: {second}, Bronze: {third}")
