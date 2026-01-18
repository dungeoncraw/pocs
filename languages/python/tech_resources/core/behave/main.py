def calculate_discount(price, coupon):
    if coupon == "SAVE10":
        return price * 0.9
    return price

if __name__ == "__main__":
    # This runs when you execute the file directly
    final_price = calculate_discount(100, "SAVE10")
    print(f"Final price with discount: {final_price}")