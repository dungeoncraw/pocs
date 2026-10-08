class_name GameManager
extends Node

signal ManaChanged (cur : int, max : int)
signal TurnBegan (character : Character)
signal TurnEnded (character : Character)

@export var player : Character
@export var enemy : Character

@onready var character_highlight : Node2D = $CharacterHighlight
@onready var enemy_spawn : Node2D = $EnemySpawn

var current_character : Character

var is_game_over : bool = false
var current_mana : int
var max_mana : int = 3

var is_player_turn : bool:
	get: return current_character == player

func _enter_tree ():
	ManagerRegistry.register("game_manager", self)

func _exit_tree ():
	ManagerRegistry.unregister("game_manager")

func _ready ():
	if GameData.current_level_data != null:
		enemy.queue_free()
		enemy = GameData.current_level_data.enemy_scene.instantiate()
		add_child(enemy)
		enemy.global_position = enemy_spawn.global_position
	
	_next_character_turn()

func _next_character_turn ():
	if current_character == null:
		current_character = player
	else:
		current_character = player if current_character == enemy else enemy
	
	if current_character == player:
		reset_mana()
	
	character_highlight.global_position = current_character.global_position
	TurnBegan.emit(current_character)

func end_character_turn ():
	TurnEnded.emit(current_character)
	_next_character_turn()

func spend_mana (amount : int):
	current_mana -= amount
	ManaChanged.emit(current_mana, max_mana)

func reset_mana ():
	current_mana = max_mana
	ManaChanged.emit(current_mana, max_mana)

func end_game (defeated_character : Character):
	is_game_over = true
	
	if defeated_character == enemy:
		if GameData.unlocked_level == GameData.current_level:
			GameData.unlocked_level += 1
		
		GameData.current_health = player.current_health
	else:
		GameData.unlocked_level = 0
	
	await get_tree().create_timer(2.0).timeout
	
	get_tree().change_scene_to_file("res://Scenes/menu.tscn")
