extends Area2D

@export var move_direction: Vector2
@export var move_speed: float = 20.0

@onready var start_pos: Vector2 = global_position
@onready var target_pos: Vector2 = global_position + move_direction

func _physics_process(delta: float) -> void:
	global_position = global_position.move_toward(target_pos, move_speed * delta)
	
	if global_position == target_pos:
		if target_pos == start_pos:
			target_pos = start_pos + move_direction
		else:
			target_pos = start_pos

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	$AnimationPlayer.play("fly")
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_body_entered(body: Node2D) -> void:
	if not body.is_in_group("Player"):
		return
	body.take_damage(1)
