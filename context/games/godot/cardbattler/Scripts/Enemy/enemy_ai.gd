class_name EnemyAI
extends Node

@export var actions: Array[CardData]
@export var cast_delay: float = 0.8
@export var end_turn_delay: float = 1.0

@onready var next_action_icon: Sprite2D = $"../NextActionIcon"
@onready var next_action_label: Label = $"../NextActionLabel"

var cur_action_index: int = -1

var game_manager: GameManager:
	get: return ManagerRegistry.get_manager("game_manager")

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	game_manager.TurnBegan.connect(_on_turn_began)
	game_manager.TurnEnded.connect(_on_turn_ended)
	
	_set_next_action()


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _on_turn_began(character: Character):
	if not character.is_player:
		_perform_current_action()

func _on_turn_ended(character: Character):
	if not character.is_player:
		_set_next_action()

func _set_next_action():
	cur_action_index += 1
	
	if cur_action_index == len(actions):
		cur_action_index = 0
	
	next_action_icon.texture = actions[cur_action_index].icon
	next_action_label.text = str(actions[cur_action_index].get_value())
	
func _perform_current_action():
	await get_tree().create_timer(cast_delay).timeout
	var cast_data: CardData.CastData = CardData.CastData.new()
	cast_data.caster = game_manager.enemy
	cast_data.opponent = game_manager.player
	
	actions[cur_action_index].cast(cast_data)
	
	await get_tree().create_timer(end_turn_delay).timeout
	
	game_manager.end_character_turn()
