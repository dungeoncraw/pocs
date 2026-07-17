extends CanvasLayer
signal place_tower(tower_type: Data.Tower)
signal start_wave

func _ready() -> void:
	$TowerCards/HBoxContainer/TowerCard.connect('press', tower_select)


func tower_select(tower_enum: Data.Tower):
	place_tower.emit(tower_enum)


func update_stats(money: int, health: int):
	$Control/StatsContainer/PanelContainer2/HBoxContainer/Label.text = str(money)
	$Control/StatsContainer/PanelContainer/HBoxContainer/Label.text = str(health)

func _on_wave_button_pressed() -> void:
	start_wave.emit()
