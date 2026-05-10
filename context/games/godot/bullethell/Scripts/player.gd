extends CharacterBody2D

@export var current_hp: int = 50
@export var max_hp: int = 50

@export var max_speed: float = 100.0
@export var acceleration: float = 0.2
@export var braking: float = 0.15


@onready var sprite: Sprite2D = $Sprite
@onready var muzzle: Node2D = $Muzzle
# 0.1 s can  shoot a bullet
@export var shoot_rate: float = 0.1

var last_shoot_time: float
var move_input: Vector2
var additional_bullet_speed: float

@onready var bullet_pool = $PlayerBulletPool
@onready var health_bar: ProgressBar = $HealthBar
@onready var shoot_audio: AudioStreamPlayer = $ShootAudio
@onready var damaged_audio: AudioStreamPlayer = $DamagedAudio
@onready var potion_audio: AudioStreamPlayer =  $PotionAudio

func _ready() -> void:
	health_bar.max_value = max_hp
	health_bar.value = current_hp
	
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
	_move_wooble()

func _move_wooble():
	if move_input.length() == 0:
		sprite.rotation_degrees = 0
		return
	var t = Time.get_unix_time_from_system()
	var rot = sin(t * 20) * 2
	sprite.rotation_degrees = rot

func _shoot():
	last_shoot_time = Time.get_unix_time_from_system()
	var bullet = bullet_pool.spawn()
	
	bullet.global_position = muzzle.global_position
	var mouse_pos = get_global_mouse_position()
	var mouse_dir = muzzle.global_position.direction_to(mouse_pos)
	
	bullet.move_dir = mouse_dir
	bullet.additional_speed = additional_bullet_speed
	shoot_audio.play()

func take_damage(damage: int):
	current_hp -= damage
	if current_hp <= 0:
		$"..".set_game_over()
	else:
		_damage_flash()
		health_bar.value = current_hp
		damaged_audio.play()
		$"../Camera2D".damage_shake()
		


func _damage_flash():
	sprite.modulate = Color.RED
	await get_tree().create_timer(0.05).timeout
	sprite.modulate = Color.WHITE

func heal(amount: int):
	current_hp += amount
	if current_hp > max_hp:
		current_hp = max_hp
	health_bar.value = current_hp

func drink_potion():
	potion_audio.play()
