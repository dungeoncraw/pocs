extends Panel

@onready var header_text: Label = $HeaderText

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func set_header_text (text_to_display: String):
	header_text.text = text_to_display


func _on_play_again_button_pressed() -> void:
	get_tree().reload_current_scene()
