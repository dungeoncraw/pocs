extends Control

@onready var height_entry = $VBoxContainer/HeightInput/PanelContainer/MarginContainer/VBoxContainer/HeightLineEdit
@onready var height_slider = $VBoxContainer/HeightInput/PanelContainer/MarginContainer/VBoxContainer/Control/HSlider
@onready var weight_entry = $VBoxContainer/WeightInput/PanelContainer/HBoxContainer/WeightLineEdit
@onready var bmi_label = $VBoxContainer/Output/Label
var height: int = 170:
	set(value):
		height = value
		update_bmi()
var weight: int = 80:
	set(value):
		weight = value
		update_bmi()

func update_bmi():
	var bmi: float = weight / (pow(height,2) * 0.01) * 100
	bmi_label.text = str(snapped(bmi, 0.1))


func _ready() -> void:
	height_entry.text = str(height)
	height_slider.value = height
	weight_entry.text = str(weight)


func _on_h_slider_value_changed(value: float) -> void:
	height = int(value)
	height_entry.text = str(int(value))


func _on_minus_button_pressed() -> void:
	weight -= 1
	weight_entry.text = str(weight)


func _on_plus_button_pressed() -> void:
	weight += 1
	weight_entry.text = str(weight)


func _on_weight_line_edit_text_changed(new_text: String) -> void:
	var old_caret_pos = weight_entry.caret_column
	var number_string = ""
	var regex = RegEx.new()
	regex.compile("[0-9]")
	var caret_difference = regex.search_all(new_text).size() - new_text.length()
	for valid_char in regex.search_all(new_text):
		number_string += valid_char.get_string()
	weight_entry.text = number_string
	weight_entry.caret_column = old_caret_pos + caret_difference
	weight = int(new_text)


func _on_height_line_edit_text_changed(new_text: String) -> void:
	var old_caret_pos = height_entry.caret_column
	var number_string = ""
	var regex = RegEx.new()
	regex.compile("[0-9]")
	var caret_difference = regex.search_all(new_text).size() - new_text.length()
	for valid_char in regex.search_all(new_text):
		number_string += valid_char.get_string()
	height_slider.value = int(new_text)
	height_entry.text = number_string
	height_entry.caret_column = old_caret_pos + caret_difference
	height = int(new_text)
