class_name AttackCardData
extends CardData

@export var attack_damage : int = 1

func get_description () -> String:
	return str("Deal ", attack_damage, " damage to target.")

func cast (data : CastData):
	data.caster.attack(data.opponent, attack_damage)

func get_value () -> int:
	return attack_damage
