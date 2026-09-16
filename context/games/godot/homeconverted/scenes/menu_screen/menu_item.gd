extends Button
class_name MenuItem

## One row in the main menu: index number, edge bar, label + hint, arrow.
## Mirrors the isActive / isPrimary styling logic from the real index.tsx.

signal activated(item: MenuItem)

@export var index_text: String = "01"
@export var label_text: String = "New Game"
@export var hint_text: String = "Begin the rupture"
@export var is_primary: bool = false

const CYAN := Color(0.161, 0.839, 0.902, 1.0)
const GOLD := Color(0.831, 0.663, 0.259, 1.0)
const FOREGROUND := Color(0.945, 0.949, 0.965, 1.0)
const MUTED := Color(0.67, 0.70, 0.76, 1.0)
const BORDER := Color(0.161, 0.839, 0.902, 0.22)
const CARD := Color(0.106, 0.106, 0.153, 0.55)
const CARD_ACTIVE := Color(0.106, 0.106, 0.153, 0.8)

@onready var row: Control = $Row
@onready var index_label: Label = %IndexLabel
@onready var edge_bar: ColorRect = %EdgeBar
@onready var label_label: Label = %LabelText
@onready var hint_label: Label = %HintText
@onready var arrow_label: Label = %ArrowLabel

var _row_base_position: Vector2


func _ready() -> void:
	index_label.text = index_text
	label_label.text = label_text.to_upper()
	hint_label.text = hint_text.to_upper()
	_row_base_position = row.position

	mouse_entered.connect(func(): activated.emit(self))
	focus_entered.connect(func(): activated.emit(self))

	set_active(false)


func set_active(value: bool) -> void:
	var accent := GOLD if is_primary else CYAN

	index_label.modulate = accent if value else MUTED
	label_label.modulate = accent if value else FOREGROUND
	edge_bar.color = accent if value else Color(0, 0, 0, 0)
	arrow_label.visible = value
	arrow_label.modulate = accent

	var style := StyleBoxFlat.new()
	style.bg_color = CARD_ACTIVE if value else CARD
	style.border_width_left = 1
	style.border_width_top = 1
	style.border_width_right = 1
	style.border_width_bottom = 1
	style.border_color = accent if value else BORDER
	# Approximates the clip-path notch on the source's bottom-left corner.
	style.corner_radius_bottom_left = 16
	if value:
		style.shadow_color = Color(accent.r, accent.g, accent.b, 0.3)
		style.shadow_size = 14

	add_theme_stylebox_override("normal", style)
	add_theme_stylebox_override("hover", style)
	add_theme_stylebox_override("pressed", style)
	add_theme_stylebox_override("focus", style)

	row.position = _row_base_position + (Vector2(8, 0) if value else Vector2.ZERO)
