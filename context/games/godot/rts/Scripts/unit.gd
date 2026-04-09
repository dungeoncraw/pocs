extends Area2D

class_name Unit

# signals
signal OnTakeDamage(health: int)
signal OnDie(unit: Unit)

enum Team {
	PLAYER,
	AI
}

@export var move_speed: float = 30.0
@export var cur_hp: int = 20
@export var max_hp: int = 20
@export var attack_range: float = 20.0
#twice per second
@export var attack_rate: float = 0.5
@export var attack_damage: int = 5
@export var team: Team


var last_attack_time: float
var attack_target: Unit

@onready var agent: NavigationAgent2D = $NavigationAgent2D

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if not agent.is_target_reached():
		_move(delta)

func _target_check():
	pass

func _try_attack_target():
	pass

func _move(delta):
	# next point in the navigation mesh
	var move_pos = agent.get_next_path_position()
	var move_dir = global_position.direction_to(move_pos)
	# move_dir * move_speed = move per second
	# move per second * delta = move per frame
	var movement = move_dir * move_speed * delta
	
	translate(movement)
	
		
func set_move_to_target(target: Vector2):
	agent.target_position = target
	# move to a position, so no target
	attack_target = null

func set_attack_target(target: Unit):
	pass
	
func take_damage(amount: int):
	pass
	
func _die():
	pass
