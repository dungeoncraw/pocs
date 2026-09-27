class_name GameManager
extends Node

signal ManaChanged(curr: int, max: int)

@export var player: Character
@export var enemy: Character

var manager_name: String = "game_manager"

var current_mana: int
@export var max_mana: int = 3

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	reset_mana()


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
