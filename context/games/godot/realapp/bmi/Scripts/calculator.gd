extends Control


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_zero_button_resized() -> void:
	#	keep the size of button and alignment
	%ZeroButton.offset_left = -get_window().size.x / 4
