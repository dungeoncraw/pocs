extends CharacterBody2D

@export var max_speed: float = 100.0
@export var acceleration: float = 0.2
@export var braking: float = 0.15


@onready var sprite: Sprite2D = $Sprite
@onready var muzzle: Node2D = $Muzzle
# 0.1 s can  shoot a bullet
@export var shoot_rate: float = 0.1

var last_shoot_time: float
var move_input: Vector2

@onready var bullet_pool = $PlayerBulletPool

func _physics_process(delta: float) -> void:
	move_input = Input.get_vector("move_left", "move_right", "move_up", "move_down")
	
	if move_input.length() > 0:
		#	increase by acceleration
		velocity = velocity.lerp(move_input * max_speed, acceleration)
	else:
		# braking until zero
		velocity = velocity.lerp(Vector2.ZERO, braking)
	move_and_slide()	

func _process(delta: float) -> void:
	sprite.flip_h = get_global_mouse_position().x > global_position.x
	
	if Input.is_action_pressed("shoot"):
		if Time.get_unix_time_from_system() - last_shoot_time > shoot_rate:
			_shoot()

func _shoot():
	last_shoot_time = Time.get_unix_time_from_system()
	var bullet = bullet_pool.spawn()
	
	bullet.global_position = muzzle.global_position
	var mouse_pos = get_global_mouse_position()
	var mouse_dir = muzzle.global_position.direction_to(mouse_pos)
	
	bullet.move_dir = mouse_dir
