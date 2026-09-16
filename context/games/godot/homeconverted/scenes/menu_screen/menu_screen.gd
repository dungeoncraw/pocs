extends Control

## Controller for the real "PANTHEON.exe" main menu, rebuilt from index.tsx.
## Mirrors the MENU array + active-index state machine from the React version.

const MenuItemScene := preload("res://scenes/menu_screen/menu_item.tscn")

const MENU_DATA := [
	{"key": "play", "label": "New Game", "hint": "Begin the rupture", "primary": true},
	{"key": "load", "label": "Continue", "hint": "Restore last covenant", "primary": false},
	{"key": "settings", "label": "Settings", "hint": "Calibrate the engine", "primary": false},
	{"key": "quit", "label": "Quit", "hint": "Sever the connection", "primary": false},
]

@onready var scanlines: ColorRect = %Scanlines
@onready var menu_list: VBoxContainer = %Menu
@onready var hover_label: Label = %HoverLabel

var _items: Array[MenuItem] = []
var _active_index := 0


func _ready() -> void:
	_sync_shader_sizes()
	resized.connect(_sync_shader_sizes)

	for i in range(MENU_DATA.size()):
		var data: Dictionary = MENU_DATA[i]
		var item := MenuItemScene.instantiate() as MenuItem
		item.index_text = "0%d" % (i + 1)
		item.label_text = data["label"]
		item.hint_text = data["hint"]
		item.is_primary = data["primary"]
		item.activated.connect(_on_item_activated.bind(i))
		item.mouse_exited.connect(_on_item_mouse_exited)
		item.pressed.connect(_on_item_pressed.bind(data["key"]))
		menu_list.add_child(item)
		_items.append(item)

	_set_active(0)


func _sync_shader_sizes() -> void:
	var mat := scanlines.material as ShaderMaterial
	if mat:
		mat.set_shader_parameter("rect_height", size.y)


func _on_item_activated(item: MenuItem, item_index: int) -> void:
	_set_active(item_index)


func _on_item_mouse_exited() -> void:
	hover_label.text = "// AWAITING INPUT"


func _set_active(item_index: int) -> void:
	_active_index = item_index
	for i in range(_items.size()):
		_items[i].set_active(i == item_index)
	hover_label.text = "// " + String(MENU_DATA[item_index]["key"]).to_upper()


func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventKey and event.pressed:
		var key_event := event as InputEventKey
		var key: Key = key_event.keycode
		if key == KEY_DOWN or key == KEY_S:
			_set_active((_active_index + 1) % _items.size())
			get_viewport().set_input_as_handled()
		elif key == KEY_UP or key == KEY_W:
			_set_active((_active_index - 1 + _items.size()) % _items.size())
			get_viewport().set_input_as_handled()
		elif key == KEY_ENTER or key == KEY_SPACE:
			_items[_active_index].pressed.emit()
			get_viewport().set_input_as_handled()


func _on_item_pressed(key: String) -> void:
	match key:
		"play":
			# TODO: get_tree().change_scene_to_file("res://scenes/game/game.tscn")
			print("New Game pressed")
		"load":
			print("Continue pressed")
		"settings":
			print("Settings pressed")
		"quit":
			get_tree().quit()
