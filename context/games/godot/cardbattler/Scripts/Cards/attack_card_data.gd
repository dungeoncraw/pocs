class_name AttackCardData
extends CardData

@export var attack_damage: int = 1

func get_description() -> String:
	return str("Deal ", attack_damage, " damage")

func cast(data: CastData):
	data.caster.attack(data.opponent, attack_damage)

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
