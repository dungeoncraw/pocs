class_name Character
extends Node2D

signal HealthUpdated

var current_health: int
@export var max_health: int = 20
@export var is_player: bool = false
@export var attack_delay: float = 0.3

@onready var anim: CharacterAnimation = $CharacterAnimation

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	current_health = max_health
	HealthUpdated.emit()
	anim.play(anim.IDLE_ANIM)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func take_damage(amount: int):
	current_health -= amount
	HealthUpdated.emit()
	
	anim.play(anim.HIT_ANIM)
	await get_tree().create_timer(attack_delay).timeout
	if current_health <= 0:
		die()
	
func attack(target: Character, damage_amount: int):
	anim.play(anim.ATTACK_ANIM)
	
	target.take_damage(damage_amount)
	
func heal(amount: int):
	current_health = clamp(current_health + amount, 0, max_health)
	HealthUpdated.emit()
	
func die():
	anim.play(anim.DEATH_ANIM)
	pass
