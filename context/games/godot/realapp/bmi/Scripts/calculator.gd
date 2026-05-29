extends Control

@onready var label = $VBoxContainer/MarginContainer/Label
var temp_nums: Array
var fin_nums: Array
var themes = [preload("res://light_theme.tres"), preload("res://black_theme.tres")]
var theme_index: int
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	label.text = ''
	for button in $VBoxContainer/PanelContainer/GridContainer.get_children():
		if button is Button:
			button.connect('pressed', button_pressed.bind(button.text))
	%ZeroButton.connect('pressed', button_pressed.bind(%ZeroButton.text))

func button_pressed(character: String):
	if character.is_valid_int():
		temp_nums.append(character)
		label.text = array_to_string(temp_nums)

	if character in ['+', '-', '/', 'x']:
		fin_nums.append(array_to_string(temp_nums))
		fin_nums.append(character if character != 'x' else '*')
		temp_nums.clear()
	if character == '=':
		fin_nums.append(array_to_string(temp_nums))
		temp_nums.clear()
		var expression = Expression.new()
		expression.parse(array_to_string(fin_nums))
		var result = expression.execute()
		label.text = str(result)
		fin_nums.clear()
		temp_nums = str(result).split('')
		
	if character == '.':
		if '.' not in temp_nums:
			temp_nums.append('.')
			label.text = array_to_string(temp_nums)
	if character == '%':
		var num = float(array_to_string(temp_nums)) * 0.01
		temp_nums = str(num).split('')
		label.text= array_to_string(temp_nums)
		
	if character == '+/-':
		if temp_nums[0] == '-':
			temp_nums.remove_at(0)
		else:
			temp_nums.insert(0, '-')
		label.text = array_to_string(temp_nums)
	if character == 'C':
		temp_nums.clear()
		fin_nums.clear()
		label.text = ''
# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_zero_button_resized() -> void:
	#	keep the size of button and alignment
	%ZeroButton.offset_left = -get_window().size.x / 4

func array_to_string(array: Array) -> String:
	var s = ''
	for i in array:
		s += str(i)
	return s

func _input(_event: InputEvent) -> void:
	if Input.is_action_just_pressed('ui_focus_next'):
		theme_index = (theme_index + 1) % 2
		theme = themes[theme_index]
		
