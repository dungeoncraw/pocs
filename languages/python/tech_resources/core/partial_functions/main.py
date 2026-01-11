# simple examples using partial functions from functools module
from functools import partial

def bake_with_flour(num_eggs: int, ml_milk: int, flour_type: str = None, fruits: list = None):
    if num_eggs == 0 and ml_milk == 0:
        return "Nothing to bake"
    if flour_type == "white":
        if fruits:
            return f"Baking with {num_eggs} eggs, {ml_milk}ml milk, white flour, and {len(fruits)} fruits, so is a fruit cake"
        else:
            return f"Baking with {num_eggs} eggs, {ml_milk}ml milk, and white flour, so is a plain cake"
    elif flour_type == "vanilla":
        if fruits:
            return f"Baking with {num_eggs} eggs, {ml_milk}ml milk, {flour_type} flour, and {len(fruits)} fruits, so is a vanilla cake with fruits"
        else:
            return f"Baking with {num_eggs} eggs, {ml_milk}ml milk, and {flour_type} flour, so is a naked vanilla cake"
    return None


bake_fruit_cake = partial(bake_with_flour, flour_type="white")
bake_naked_cake = partial(bake_with_flour, flour_type="white", fruits=[])
bake_vanilla_cake = partial(bake_with_flour, flour_type="vanilla")
bake_nothing = partial(bake_with_flour, num_eggs=0, ml_milk=0)

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print(bake_fruit_cake(2, ml_milk=100, fruits=["apple", "banana"]))
    print(bake_naked_cake(2, 100))
    print(bake_vanilla_cake(num_eggs=2, ml_milk=100))
    print(bake_nothing())
