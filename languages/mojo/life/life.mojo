from gridv1 import Grid
from python import Python
import time

def run_display(
    var grid: Grid,
    window_height: Int = 600,
    window_width: Int = 600,
    background_color: String = "black",
    cell_color: String = "green",
    pause: Float64 = 0.1,
) -> None:
    var pygame = Python.import_module("pygame")

    pygame.init()

    var window = pygame.display.set_mode(Python.tuple(window_width, window_height))
    pygame.display.set_caption("Conway's Game of Life")

    var cell_height = window_height / grid.rows
    var cell_width = window_width / grid.cols
    var border_size = 1
    var cell_fill_color = pygame.Color(cell_color)
    var background_fill_color = pygame.Color(background_color)

    var running = True
    while running:
        var event = pygame.event.poll()
        if event.type == pygame.QUIT:
            # Quit if the window is closed
            running = False
        elif event.type == pygame.KEYDOWN:
            # Also quit if the user presses <Escape> or 'q'
            if event.key == pygame.K_ESCAPE or event.key == pygame.K_q:
                running = False

        window.fill(background_fill_color)

        for row in range(grid.rows):
            for col in range(grid.cols):
                if grid[row, col]:
                    var x = col * cell_width + border_size
                    var y = row * cell_height + border_size
                    var width = cell_width - border_size
                    var height = cell_height - border_size
                    pygame.draw.rect(
                        window,
                        cell_fill_color,
                        Python.tuple(x, y, width, height),
                    )

        pygame.display.flip()

        time.sleep(pause)

        grid = grid.evolve().copy()

    pygame.quit()


def main():
    var start = Grid.random(128, 128)
    run_display(start^)