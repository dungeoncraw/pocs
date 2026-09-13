class_name HealCardData
extends CardData

@export var heal_amount: int = 1

func get_description() -> String:
	return str("Heal ", heal_amount, " health")
	
func cast(data: CastData):
	data.caster.heal(heal_amount)

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
