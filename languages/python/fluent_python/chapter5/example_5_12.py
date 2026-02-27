from dataclasses import dataclass

@dataclass
class DemoDataClass:
    a: int
    b: float = 1.1
    c = 'spam'


if __name__ == "__main__":
    print(DemoDataClass.__annotations__)
    print(DemoDataClass.__doc__)
    try:
        DemoDataClass.a
    except AttributeError as e:
        print(f"AttributeError: {e}")
    print(DemoDataClass.b)

    dc = DemoDataClass(9)
    print(dc.a)
    print(dc.b)
    print(dc.c)
