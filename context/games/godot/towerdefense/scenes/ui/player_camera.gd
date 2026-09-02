extends Camera2D

var drag: bool
@export var acceleration: float = 0.4
@export var camera_speed: int = 100
func _input(event: InputEvent) -> void:
	if event is InputEventMouseButton and event.button_index == 3:
		drag = event.pressed
	if event is InputEventMouseMotion:
		if drag:
			position -= event.relative * acceleration

func _process(delta: float) -> void:
	if Input.is_action_pressed("down"):
		position.y += delta * camera_speed
	if Input.is_action_pressed("up"):
		position.y -= delta * camera_speed
	if Input.is_action_pressed("left"):
		position.x -= delta * camera_speed
	if Input.is_action_pressed("right"):
		position.x += delta * camera_speed
