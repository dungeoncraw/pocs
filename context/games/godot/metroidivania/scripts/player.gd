extends CharacterBody2D

var health: int = 10:
	set(value):
		health = value
		$UI.set_health(value)

var direction_x: float
var duck: bool
var on_floor: bool
@export_category('move')
@export var speed: int = 120
@export var acceleration: int = 600
@export var friction: int = 800
@export var dash_speed: int = 600
# player jumping script
@export_category('jump')
@export var jump_height: float = 100
@export var jump_time_to_peak: float = 0.5
@export var jump_time_to_descent: float = 0.4
@export_category('shooting')
@export var crosshair_distance: int = 50

@onready var jump_velocity: float = ((2.0 * jump_height) / jump_time_to_peak) * -1.0
@onready var jump_gravity: float = ((-2.0 * jump_height) / (jump_time_to_peak * jump_time_to_peak)) * -1.0
@onready var fall_gravity: float = ((-2.0 * jump_height) / (jump_time_to_peak * jump_time_to_descent)) * -1.0

#signals
signal shoot(pos: Vector2, dir: Vector2)

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
	if Input.is_action_just_pressed("jump") and (is_on_floor() or $Timer/CoyoteTimer.time_left):
		velocity.y = jump_velocity
	if Input.is_action_just_pressed("shoot") and not $Timer/ReloadTimer.time_left:
		shoot.emit(position, get_local_mouse_position().normalized())
		$Timer/ReloadTimer.start()
	if Input.is_action_just_pressed("dash") and not $Timer/DashTimer.time_left:
		$Timer/DashTimer.start()
		var tween = create_tween()
		tween.tween_property(self, 'velocity:x', velocity.x + direction_x * dash_speed, 0.3)
		tween.tween_callback(_dash_finish)
	duck = Input.is_action_pressed("duck") and is_on_floor()
	
func _dash_finish():
	velocity.x = move_toward(velocity.x, 0, 500)

func _ready() -> void:
	$UI.set_health(health)

func _process(_delta: float) -> void:
	$Sprites/Crosshair.update(get_local_mouse_position().normalized(), crosshair_distance, duck)
	if on_floor and not is_on_floor() and velocity.y >=0:
		$Timer/CoyoteTimer.start()
	
func animation():
	#legs
	if direction_x != 0:
		$Sprites/LegSprite.flip_h = direction_x < 0
	if is_on_floor():
		$AnimationPlayer.current_animation = 'run' if direction_x else 'idle'
		$AnimationPlayer.current_animation = 'duck' if duck else $AnimationPlayer.current_animation
	else:
		$AnimationPlayer.current_animation = 'jump'
	#torso
	var raw_direction = get_local_mouse_position().normalized()
	var adjusted_dir = Vector2i(round(raw_direction.x), round(raw_direction.y))
	$Sprites/TorsoSprite.frame = GUN_DIRECTIONS[adjusted_dir]
	$Sprites/TorsoSprite.position.y = 0 if duck else  -8
	
func move(delta: float):
	if not duck:
		if direction_x:
			# move increasing velocity
			velocity.x = move_toward(velocity.x, direction_x * speed, acceleration * delta)
		else:
			# decrease velocity
			velocity.x = move_toward(velocity.x, 0, friction * delta)
	else:
		velocity.x = move_toward(velocity.x, 0, friction * delta * 2)
	if not is_on_floor():
		velocity.y += get_custom_gravity() * delta

func get_custom_gravity() -> float:
	return jump_gravity if velocity.y < 0.0 else fall_gravity
		
func _physics_process(delta: float) -> void:
	get_input()
	move(delta)
	animation()
	# physics process run first, then after the process to update the sprites
	on_floor = is_on_floor()
	move_and_slide()

func hit():
	health -= 1
