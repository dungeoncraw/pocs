extends Button

var id: Data.Tower = Data.Tower.BASIC
var cost: int
signal press(tower_enum: Data.Tower)

func _ready() -> void:
	cost = Data.TOWER_DATA[Data.Tower.BASIC]['cost']
	toggle_active(Data.money)

func toggle_active(money: int):
	disabled = cost > money

func _on_pressed() -> void:
	press.emit(id)
