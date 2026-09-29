class_name GameManager
extends Node

signal ManaChanged(curr: int, max: int)
signal TurnBegan(character: Character)
signal TurnEnded(character: Character)

@export var player: Character
@export var enemy: Character
@export var max_mana: int = 3

@onready var character_highligth: Node2D = $CharacterHighlight

var manager_name: String = "game_manager"
var current_character: Character
var current_mana: int

var is_player_turn: bool:
	get: return current_character == player


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	_next_character_turn()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _enter_tree() -> void:
	ManagerRegistry.register(manager_name, self)

func _exit_tree() -> void:
	ManagerRegistry.unregister(manager_name)

func spend_mana(amount: int):
	current_mana -= amount
	ManaChanged.emit(current_mana, max_mana)
	
func reset_mana():
	current_mana = max_mana
	ManaChanged.emit(current_mana, max_mana)

func _next_character_turn():
	if current_character == null:
		current_character = player
	else:
		current_character = player if current_character == enemy else enemy
	
	if current_character == player:
		reset_mana()

	character_highligth.global_position = current_character.global_position
	TurnBegan.emit(current_character)

func end_character_turn():
	TurnEnded.emit(current_character)
	_next_character_turn()
