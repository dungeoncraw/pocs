extends RigidBody3D

@export var move_speed : float = 2.0
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _physics_process(delta: float) -> void:
	if Input.is_physical_key_pressed(KEY_LEFT):
		# Vector3.left generates a Vector(-1, 0, 0)
		apply_force(Vector3.RIGHT * move_speed)
	
	elif Input.is_physical_key_pressed(KEY_RIGHT):
		apply_force(Vector3.LEFT * move_speed)


func _on_body_entered(body: Node) -> void:
	#enables the Solver -> Contact monitor in order to capture the hit
	if body.is_in_group("Tree"):
		# this restart current scene, using call_deferred happens when current frame ends
		get_tree().reload_current_scene.call_deferred()
