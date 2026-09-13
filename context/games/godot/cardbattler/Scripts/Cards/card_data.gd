@abstract
class_name CardData
extends Resource

@export var display_name: String
@export var cost: int = 1
@export var icon: Texture2D

@abstract
func get_description() -> String

@abstract
func cast(data: CastData)

class CastData:
	var caster: Character
	var opponent: Character


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
