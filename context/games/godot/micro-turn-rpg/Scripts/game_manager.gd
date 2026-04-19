extends Node2D

@export var player_character: Character
@export var ai_character: Character
@onready var player_ui: Panel = $CanvasLayer/CombatActionsUI
@onready var end_screen: Panel = $CanvasLayer/EndScreen
var current_character: Character

var game_over: bool = false

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	player_character.OnTakeDamage.connect(_on_player_take_damage)
	ai_character.OnTakeDamage.connect(_on_ai_take_damage)
	end_screen.visible = false
	next_turn()
	
# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _on_player_take_damage(health: int):
	if health <= 0:
		end_game(ai_character)

func end_game(winner: Character):
	game_over = true
	end_screen.visible = true
	if winner == player_character:
		end_screen.set_header_text("You have won the game!")
	else:
		end_screen.set_header_text("AI have won the game!")

func _on_ai_take_damage(health: int):
	if health <= 0:
		end_game(player_character)
	

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
		player_ui.visible = true
		player_ui.set_combat_actions(player_character.combat_actions)
	else:
		player_ui.visible = false
		var wait_time = randf_range(0.5, 1.5)
		await get_tree().create_timer(wait_time).timeout
		var action_to_cast = ai_decide_combat_action()
		ai_character.cast_combat_action(action_to_cast, player_character)
		await get_tree().create_timer(0.5).timeout
		next_turn()

func player_cast_combat_action(action: CombatAction):
	if player_character != current_character:
		return
	player_character.cast_combat_action(action, ai_character)
	player_ui.visible = false
	await get_tree().create_timer(0.5).timeout
	next_turn()
	
	
func ai_decide_combat_action() -> CombatAction:
	if ai_character !=  current_character:
		return null
	# shorthands variables
	var ai: Character = ai_character
	var player: Character = player_character
	var actions: Array[CombatAction] = ai.combat_actions
	
	var weights: Array[int] = []
	var total_weight: int = 0
	
	var ai_health_per: float = float(ai.cur_health) / float(ai.max_health)
	var player_health_per: float = float(player.cur_health) / float(player.max_health)
	
	for action in actions:
		var weight: int = action.base_weight
		if player.cur_health <= action.melee_damage:
			weight *= 3
		if action.heal_amount > 0:
			weight *= 1 +(1 - ai_health_per)
		weights.append(weight)
		total_weight += weight
	
	var cumulative_weight = 0
	var random_weight = randi_range(0, total_weight)
	
	for i in len(actions):
		cumulative_weight += weights[i]
		
		if random_weight < cumulative_weight:
			return actions[i]
	return null
