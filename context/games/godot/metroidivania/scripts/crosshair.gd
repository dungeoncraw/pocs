extends Sprite2D


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func update(direction: Vector2, distance: int, duck: bool):
	var offset_y = 2 if duck else -6
	position = direction * distance + Vector2(0, offset_y)
