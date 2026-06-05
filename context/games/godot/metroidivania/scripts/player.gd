extends CharacterBody2D

var direction_x: float
var gravity = 200
@export var speed: int = 120

# player gun directions
const GUN_DIRECTIONS = {
	Vector2i(0,0):   0,
	Vector2i(1,0):   0,
	Vector2i(1,1):   1, 
	Vector2i(0,1):   2,
	Vector2i(-1,1):  3,
	Vector2i(-1,0):  4,
	Vector2i(-1,-1): 5,
	Vector2i(0,-1):  6,
	Vector2i(1,-1):  7,
}

func get_input():
	direction_x = Input.get_axis("left", "right")
	if Input.is_action_just_pressed("jump") and is_on_floor():
		velocity.y = -250

func animation():
	#legs
	if direction_x != 0:
		$Sprites/LegSprite.flip_h = direction_x < 0
	if is_on_floor():
		$AnimationPlayer.current_animation = 'run' if direction_x else 'idle'
	else:
		$AnimationPlayer.current_animation = 'jump'
	#torso
	var raw_direction = get_local_mouse_position().normalized()
	var adjusted_dir = Vector2i(round(raw_direction.x), round(raw_direction.y))
	$Sprites/TorsoSprite.frame = GUN_DIRECTIONS[adjusted_dir]
	
func move(delta: float):
	velocity.x = direction_x * speed
	if not is_on_floor():
		velocity.y += gravity * delta
		
func _physics_process(delta: float) -> void:
	get_input()
	move(delta)
	animation()
	move_and_slide()
