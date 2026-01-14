class InvalidCommand(Exception):
    pass


class Led:
    def set_brightness(self, ident, intensity):
        print(f"LED {ident} brightness set to {intensity}")

    def set_color(self, ident, red, green, blue):
        print(f"LED {ident} color set to R:{red} G:{green} B:{blue}")


class Robot:
    def __init__(self):
        # Initialize with some Led objects to avoid IndexError
        self.leds = [Led(), Led(), Led()]

    def handle_command(self, message):
        match message:
            case ['BEEPER', frequency, times]:
                self.beep(times, frequency)
            case ['NECK', angle]:
                self.rotate_neck(angle)
            case ['LED', ident, intensity]:
                self.leds[ident].set_brightness(ident, intensity)
            case ['LED', ident, red, green, blue]:
                self.leds[ident].set_color(ident, red, green, blue)
            case _:
                raise InvalidCommand(message)

    def beep(self, times, frequency):
        print(f"Beeping {times} times at {frequency}Hz")

    def rotate_neck(self, angle):
        print(f"Rotating neck to {angle} degrees")


if __name__ == '__main__':
    robot = Robot()
    print(robot.handle_command(['BEEPER', 2, 3]))
    print(robot.handle_command(['NECK', 90]))
    print(robot.handle_command(['LED', 0, 50]))
    print(robot.handle_command(['LED', 1, 255, 0, 0]))