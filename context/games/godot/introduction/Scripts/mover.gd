extends Sprite2D

var speed : float = 70.0
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	print(position)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	#var speed_per_time = (speed * delta)
	#position = Vector2(position.x + speed_per_time,  position.y + speed_per_time)
	var direction = Vector2(1, 1)
	position += direction * delta * speed
