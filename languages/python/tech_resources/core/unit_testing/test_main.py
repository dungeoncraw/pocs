import unittest
from main import Inventory

class TestInventoryAdvanced(unittest.TestCase):

    def setUp(self):
        self.inventory = Inventory()

    def test_add_new_product(self):
        self.inventory.add_product("Phone", 500, 10)
        self.assertIn("Phone", self.inventory.items)
        self.assertEqual(self.inventory.items["Phone"]["quantity"], 10)

    def test_add_existing_product_updates_quantity(self):
        self.inventory.add_product("Phone", 500, 10)
        self.inventory.add_product("Phone", 500, 5)
        self.assertEqual(self.inventory.items["Phone"]["quantity"], 15)

    def test_total_value_calculation(self):
        self.inventory.add_product("Laptop", 1000, 2)
        self.inventory.add_product("Mouse", 50, 10)
        self.assertEqual(self.inventory.get_total_value(), 2500)

    def test_remove_product_successfully(self):
        self.inventory.add_product("Monitor", 300, 5)
        self.inventory.remove_product("Monitor", 3)
        self.assertEqual(self.inventory.items["Monitor"]["quantity"], 2)

    def test_remove_product(self):
        self.inventory.add_product("Cable", 10, 1)
        self.inventory.remove_product("Cable", 1)
        self.assertNotIn("Cable", self.inventory.items)

    def test_error_on_insufficient_stock(self):
        self.inventory.add_product("GPU", 800, 1)
        with self.assertRaises(ValueError) as context:
            self.inventory.remove_product("GPU", 2)
        self.assertEqual(str(context.exception), "Not enough stock")

    def test_error_on_negative_values(self):
        with self.assertRaises(ValueError):
            self.inventory.add_product("Wrong", -10, 1)

if __name__ == '__main__':
    unittest.main()