class Gizmo:
    def __init__(self):
        print(f'Initializing gizmo id {id(self)}')

if __name__ == "__main__":
    x = Gizmo()
    y = Gizmo() * 10