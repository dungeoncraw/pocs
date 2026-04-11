extends Panel

@onready var header_text: Label = $HeaderText

func _set_screen(winning_team: String):
	visible = true
	header_text.text = winning_team + " team has won!"

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_menu_pressed() -> void:
	pass # Replace with function body.
