class_name HealCardData
extends CardData

@export var heal_amount : int = 1

func get_description () -> String:
	return str("Heal ", heal_amount, " health.")

func cast (data : CastData):
	data.caster.heal(heal_amount)

func get_value () -> int:
	return heal_amount
