extends Sprite2D

@onready var unit: Unit = get_parent()

var unit_post_last_frame: Vector2

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	unit.OnTakeDamage.connect(_damage_flash)
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	var time = Time.get_unix_time_from_system()
	var r = sin(time * 10) * 4
	if unit.global_position.distance_to(unit_post_last_frame) == 0:
		r = 0
	rotation = deg_to_rad(r)
	var dir = unit.global_position.x - unit_post_last_frame.x
	if dir > 0:
		flip_h = false
	elif dir < 0:
		flip_h = true
	unit_post_last_frame = unit.global_position

func _damage_flash(health: int):
	modulate = Color.RED
	
	await get_tree().create_timer(0.05).timeout
	
	modulate = Color.WHITE
