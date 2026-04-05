extends Camera3D

var intensity: float = 0

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	get_parent().OnTakeDamage.connect(_damage_shake)
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if intensity > 0:
		intensity = lerp(intensity, 0.0, delta * 10)
		var offset: Vector2 = _get_random_offset()
		h_offset = offset.x
		v_offset = offset.y

func _damage_shake(health: int):
	intensity = 0.2
	

func _get_random_offset() -> Vector2:
	var x = randf_range(-intensity, intensity)
	var y = randf_range(-intensity, intensity)
	return Vector2(x, y)
