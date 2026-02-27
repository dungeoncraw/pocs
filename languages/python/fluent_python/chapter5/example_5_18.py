from dataclasses import InitVar, dataclass


class DatabaseType:
    def lookup(self, param):
        pass


@dataclass
class C:
    i: int
    j: int = None
    database: InitVar[DatabaseType] = None
    def __post_init__(self, database):
        if self.j is None and database is not None:
            self.j = database.lookup('j')


if __name__ == "__main__":
    c = C(1, database=DatabaseType())
    print(c)