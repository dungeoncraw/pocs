extends Area2D

var direction: Vector2
var speed: int = 200
const OFFSET = 16
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _physics_process(delta: float) -> void:
	position += direction * speed * delta

func setup(pos: Vector2, dir: Vector2):
	position = pos + dir * OFFSET
	direction = dir
