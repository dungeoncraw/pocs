extends Area3D

@export var speed: float = 10.0
@export var max_distance: float = 50.0
var start_pos: Vector3

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	start_pos = position


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	translate(basis.z * speed * delta)
	if position.distance_to(start_pos) > max_distance:
		position = start_pos
