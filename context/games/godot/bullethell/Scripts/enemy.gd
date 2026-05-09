extends CharacterBody2D

@export var current_hp: int = 5
@export var max_hp: int = 5

@export var max_speed: float
@export var acceleration: float
@export var drag: float
@export var stop_range: float
@export var shoot_rate: float
@export var shoot_range: float
@export var flip_sprite: bool = false

var last_shoot_time: float

@onready var player = get_tree().get_first_node_in_group("Player")
@onready var avoidance_ray : RayCast2D = $AvoidanceRay
@onready var sprite: Sprite2D = $Sprite
@onready var bullet_pool = $EnemyBulletPool
@onready var muzzle = $Muzzle
@onready var health_bar: ProgressBar = $HealthBar

var player_dist: float
var player_dir: Vector2

func _ready() -> void:
	health_bar.max_value = max_hp
	health_bar.value = current_hp

func _process(delta: float) -> void:
	player_dist = global_position.distance_to(player.global_position)
	player_dir =  global_position.direction_to(player.global_position)
	if flip_sprite:
		sprite.flip_h = player_dir.x < 0
	else:
		sprite.flip_h = player_dir.x < 0
	if player_dist < shoot_range:
		if Time.get_unix_time_from_system() - last_shoot_time > shoot_rate:
			_shoot()
	_move_wooble()

func _move_wooble():
	if velocity.length() == 0:
		sprite.rotation_degrees = 0
		return
	var t = Time.get_unix_time_from_system()
	var rot = sin(t * 20) * 2
	sprite.rotation_degrees = rot

func _physics_process(delta: float) -> void:
	var move_direction = player_dir
	var local_avoidance = _local_avoidance()
	
	if local_avoidance.length() > 0:
		move_direction = local_avoidance
	if velocity.length() < max_speed and player_dist > stop_range:
		velocity += move_direction * acceleration
	else:
		velocity *= drag
	
	move_and_slide()

func _local_avoidance() -> Vector2:
	avoidance_ray.target_position = to_local(player.global_position).normalized()
	avoidance_ray.target_position *= 80
	if not avoidance_ray.is_colliding():
		return Vector2.ZERO
	var obstacle = avoidance_ray.get_collider()
	
	if obstacle == player:
		return Vector2.ZERO
	
	var obstacle_point = avoidance_ray.get_collision_point()
	var obstacle_dir = global_position.direction_to(obstacle_point)
	return Vector2(-obstacle_dir.y, obstacle_dir.x)

func _shoot():
	last_shoot_time = Time.get_unix_time_from_system()
	var bullet = bullet_pool.spawn()
	bullet.global_position = muzzle.global_position
	bullet.move_dir = muzzle.global_position.direction_to(player.global_position)

func take_damage(damage: int):
	current_hp -= damage
	if current_hp <= 0:
		visible = false
	else:
		_damage_flash()
		health_bar.value = current_hp

func _damage_flash():
	sprite.modulate = Color.BLACK
	await get_tree().create_timer(0.05).timeout
	sprite.modulate = Color.WHITE

func _on_visibility_changed() -> void:
	if visible:
		set_process(true)
		set_physics_process(true)
		current_hp = max_hp
		if health_bar:
			health_bar.value = current_hp
	else:
		# stop processing the current node	
		set_process(false)
		set_physics_process(false)
		global_position = Vector2(0, 9999999999)
