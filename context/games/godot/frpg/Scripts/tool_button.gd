class_name ToolButton
extends TextureButton

@export var tool: PlayerTools.Tool
@export var seed: CropData
@onready var quantity_text: Label = $QuantityText
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	quantity_text.text = ""
	# change the pivot or the anchor point for the button
	pivot_offset = size / 2


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_pressed() -> void:
	pass # Replace with function body.


func _on_mouse_entered() -> void:
	scale.x = 1.05
	scale.y = 1.05


func _on_mouse_exited() -> void:
	scale.x = 1
	scale.y = 1

func _on_change_seed_quantity(crop_data: CropData, quantity: int):
	if seed != crop_data:
		return
	quantity_text.text = str(quantity)
