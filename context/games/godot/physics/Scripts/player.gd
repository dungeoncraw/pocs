extends RigidBody2D

var hit_force: float = 50.0

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if Input.is_mouse_button_pressed(MOUSE_BUTTON_LEFT):
		# this is easy, current position of mouse
		var mouse_position: Vector2 = get_global_mouse_position()
		# current position of attached node
		var dir = global_position.direction_to(mouse_position)
		# apply the force to user
		apply_impulse(dir * hit_force)
