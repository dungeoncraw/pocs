from faker import Faker

def generate_fake_names(count):
    fake = Faker()

    return [fake.name() for _ in range(count)]