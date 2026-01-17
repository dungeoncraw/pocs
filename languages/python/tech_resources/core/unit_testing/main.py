class Inventory:
    def __init__(self):
        self.items = {}

    def add_product(self, name, price, quantity):
        if price < 0 or quantity < 0:
            raise ValueError("Price and quantity must be non-negative")

        if name in self.items:
            self.items[name]['quantity'] += quantity
        else:
            self.items[name] = {'price': price, 'quantity': quantity}

    def get_total_value(self):
        return sum(item['price'] * item['quantity'] for item in self.items.values())

    def remove_product(self, name, quantity):
        if name not in self.items:
            raise KeyError("Product not found")

        if quantity > self.items[name]['quantity']:
            raise ValueError("Not enough stock")

        self.items[name]['quantity'] -= quantity
        if self.items[name]['quantity'] == 0:
            del self.items[name]


if __name__ == '__main__':
    inv = Inventory()
    inv.add_product("Laptop", 1200, 5)
    print(f"Total Inventory Value: ${inv.get_total_value()}")