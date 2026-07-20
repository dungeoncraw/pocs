extends Button

var id: Data.Tower = Data.Tower.BASIC
var cost: int
signal press(tower_enum: Data.Tower)

func setup(new_id: Data.Tower):
	id = new_id
	$VBoxContainer/Control/VBoxContainer/Label.text = Data.TOWER_DATA[id]['name']
	$VBoxContainer/Control/VBoxContainer/Label2.text = str(Data.TOWER_DATA[id]['cost'])
	$VBoxContainer/TowerPreview/TextureRect.texture = load(Data.TOWER_DATA[id]['thumbnail'])


func _ready() -> void:
	cost = Data.TOWER_DATA[Data.Tower.BASIC]['cost']
	toggle_active(Data.money)


func toggle_active(money: int):
	disabled = cost > money


func _on_pressed() -> void:
	press.emit(id)
