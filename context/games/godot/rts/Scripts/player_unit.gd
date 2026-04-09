extends Node

@onready var selection_visual: Sprite2D = $"../SelectionVisual"

func toggle_selection_visual(toggle: bool):
	selection_visual.visible = toggle

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
