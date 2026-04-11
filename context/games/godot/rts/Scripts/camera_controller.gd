extends Camera2D

@export var move_speed: float = 70.0
@export var zoom_amount: float = 0.2

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	_move(delta)
	_zoom(delta)

func _move(delta):
	var input = Input.get_vector("cam_left","cam_right","cam_up", "camp_down")
	# why 6? clamp between 1 and 5
	var zoom_mod = 6.0  - zoom.x
	global_position += input * delta * move_speed * zoom_mod
	

func _zoom(delta):
	var z = zoom.x
	
	if Input.is_action_just_released("zoom_in"):
		z += zoom_amount
	elif Input.is_action_just_released("zoom_out"):
		z -= zoom_amount
	# return the value in between min 1.0 and max 5.0
	z = clamp(z, 1.0, 5.0)
	
	zoom.x = z
	zoom.y = z
