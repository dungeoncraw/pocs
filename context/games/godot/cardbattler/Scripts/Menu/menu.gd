extends Node2D

@export var level_buttons : Array[Button]

func _ready ():
	# Only enable buttons for levels we have unlocked
	for i in len(level_buttons):
		if i <= GameData.unlocked_level:
			level_buttons[i].disabled = false
			level_buttons[i].pressed.connect(_on_level_button.bind(i, level_buttons[i].level_data))
		else:
			level_buttons[i].disabled = true

func _on_level_button (level : int, level_data : LevelData):
	GameData.current_level = level
	GameData.current_level_data = level_data
	get_tree().change_scene_to_file("res://Scenes/battle.tscn")
