import random

@fieldwise_init
struct Grid(Copyable, Stringable):
    var rows: Int
    var cols: Int
    var data: List[List[Int]]

    @staticmethod
    fn random(rows: Int, cols: Int) -> Self:
        # Mojo random uses by default a fixed seed, so if need a different random value, need to generate a new seed
        random.seed()

        var data: List[List[Int]] = []

        for _ in range(rows):
            var row_data: List[Int] = []
            for _ in range(cols):
                # Generate a random 0 or 1 and append it to the row.
                row_data.append(Int(random.random_si64(0, 1)))
            data.append(row_data^)

        return Self(rows, cols, data^)

    fn __str__(self) -> String:
        # Create an empty String
        var str = String()

        # Iterate through rows 0 through rows-1
        for row in range(self.rows):
            # Iterate through columns 0 through cols-1
            for col in range(self.cols):
                if self[row, col] == 1:
                    str += "*"  # If cell is populated, append an asterisk
                else:
                    str += " "  # If cell is not populated, append a space
            if row != self.rows - 1:
                str += "\n"     # Add a newline between rows, but not at the end
        return str

    fn __getitem__(self, row: Int, col: Int) -> Int:
        return self.data[row][col]

    fn __setitem__(mut self, row: Int, col: Int, value: Int) -> None:
        self.data[row][col] = value

    fn evolve(self) -> Self:
        var next_generation = List[List[Int]]()

        for row in range(self.rows):
            var row_data = List[Int]()

            # Calculate neighboring row indices
            var row_above = (row - 1) % self.rows
            var row_below = (row + 1) % self.rows

            for col in range(self.cols):
                # Calculate neighboring column indices
                var col_left = (col - 1) % self.cols
                var col_right = (col + 1) % self.cols

                # Define populated cells
                var num_neighbors = (
                    self[row_above, col_left]
                    + self[row_above, col]
                    + self[row_above, col_right]
                    + self[row, col_left]
                    + self[row, col_right]
                    + self[row_below, col_left]
                    + self[row_below, col]
                    + self[row_below, col_right]
                )

                # Determine the state for the next generation
                var new_state = 0
                if self[row, col] == 1 and (
                    num_neighbors == 2 or num_neighbors == 3
                ):
                    new_state = 1
                elif self[row, col] == 0 and num_neighbors == 3:
                    new_state = 1
                row_data.append(new_state)

            next_generation.append(row_data^)

        return Self(self.rows, self.cols, next_generation^)

    fn copy(self) -> Self:
        return Self(self.rows, self.cols, self.data.copy())