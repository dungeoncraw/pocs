extends Node2D

@export var player_character: Character
@export var ai_character: Character

var current_character: Character

var game_over: bool = false

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	next_turn()
	


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func next_turn():
	if game_over:
		return
	
	if current_character != null:
		current_character.end_turn()
	
	if current_character == ai_character or current_character == null:
		current_character = player_character
	else:
		current_character = ai_character
		
	current_character.begin_turn()
	
	if current_character.is_player:
		pass
	else:
		var wait_time = randf_range(0.5, 1.5)
		await get_tree().create_timer(wait_time).timeout
		
		await get_tree().create_timer(0.5).timeout
		next_turn()

func player_cast_combat_action(action):
	if player_character != current_character:
		return
	player_character.cast_combat_action(action, ai_character)
	
	await get_tree().create_timer(0.5).timeout
	next_turn()
	
	
func ai_decide_combat_action():
	pass
