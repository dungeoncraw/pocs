class_name UIManager
extends Node

@onready var mana_label: Label = $ManaUI/ManaLabel

var game_manager: GameManager:
	get: return ManagerRegistry.get_manager("game_manager")

func _enter_tree() -> void:
	ManagerRegistry.register("ui_manager", self)
	
func _exit_tree() -> void:
	ManagerRegistry.unregister("ui_manager")

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	game_manager.ManaChanged.connect(_on_mana_changed)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _on_mana_changed(cur: int, max: int):
	mana_label.text = str(cur, "/", max)
	
	if cur == 0:
		mana_label.self_modulate = Color.ORANGE_RED
	else:
		mana_label.self_modulate = Color.WHITE
