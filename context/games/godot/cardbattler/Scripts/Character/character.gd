class_name Character
extends Node2D

signal HealthUpdated

var current_health : int
@export var max_health : int = 20
@export var is_player : bool = false
@export var attack_delay : float = 0.3

@export var damaged_sfx : AudioStream
@export var healed_sfx : AudioStream

@onready var anim : CharacterAnimation = $CharacterAnimation
@onready var heal_particle : CPUParticles2D = $HealParticle

var audio_manager : AudioManager:
	get: return ManagerRegistry.get_manager("audio_manager")

func _ready ():
	if is_player and GameData.current_health > 0:
		current_health = GameData.current_health
	else:
		current_health = max_health
	
	HealthUpdated.emit()
	anim.play(anim.IDLE_ANIM)

# Take damage when opponent has casted an attack card
func take_damage (amount : int):
	current_health -= amount
	HealthUpdated.emit()
	
	anim.play(anim.HIT_ANIM)
	audio_manager.play(damaged_sfx)
	
	ManagerRegistry.get_manager("camera_controller").shake(3)
	
	if current_health <= 0:
		die()

# Called when an attack card is casted
func attack (target : Character, damage_amount : int):
	anim.play(anim.ATTACK_ANIM)
	await get_tree().create_timer(attack_delay).timeout
	target.take_damage(damage_amount)

# Called when a heal card is casted
func heal (amount : int):
	current_health = clamp(current_health + amount, 0, max_health)
	heal_particle.emitting = true
	audio_manager.play(healed_sfx)
	HealthUpdated.emit()

# Called when health reaches 0 - end the battle
func die ():
	anim.play(anim.DEATH_ANIM)
	ManagerRegistry.get_manager("game_manager").end_game(self)
