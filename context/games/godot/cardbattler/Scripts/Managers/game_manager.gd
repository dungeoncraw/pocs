class_name GameManager
extends Node

@export var player: Character
@export var enemy: Character

var manager_name: String = "game_manager"
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _enter_tree() -> void:
	ManagerRegistry.register(manager_name, self)

func _exit_tree() -> void:
	ManagerRegistry.unregister(manager_name)
