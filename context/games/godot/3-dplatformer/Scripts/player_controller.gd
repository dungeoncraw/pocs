extends CharacterBody3D

@export var health: int = 3

@export var move_speed: float = 3.0
@export var jump_force: float = 8.0
@export var gravity: float = 20.0

@onready var camera: Camera3D = $Camera3D

func _process(delta: float) -> void:
	if global_position.y < -5:
		_game_over()

func _physics_process(delta: float) -> void:
	# Add the gravity.
	velocity.y -= gravity * delta
	#if not is_on_floor():
		#velocity += get_gravity() * delta
	# jump
	if Input.is_action_pressed("jump") and is_on_floor():
		velocity.y = jump_force
	# movement
	var move_input: Vector2 = Input.get_vector("move_right", "move_left","move_back", "move_forward")
	var move_dir: Vector3 = Vector3(move_input.x, 0, move_input.y)
	velocity.x = move_dir.x * move_speed
	velocity.z = move_dir.z * move_speed
	
	move_and_slide()


func take_damage(amount: int):
	health -= amount
	
	if health <= 0:
		_game_over()

func _game_over():
	get_tree().reload_current_scene()
