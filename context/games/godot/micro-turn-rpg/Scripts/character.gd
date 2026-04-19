class_name Character

extends Node2D

signal OnTakeDamage(health: int)
signal OnHeal(health: int)


@export var is_player: bool
@export var cur_health: int
@export var max_health: int

@export var combat_actions: Array[CombatAction]
var target_scale: float = 1.0

@onready var audio: AudioStreamPlayer = $AudioStreamPlayer
var take_damage_sfx: AudioStream = preload("res://Audio/take_damage.wav")
var heal_sfx: AudioStream = preload("res://Audio/heal.wav")

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func begin_turn():
	target_scale = 1.1
	if is_player:
		print("player tur")
	else:
		print("ai turn")
func end_turn():
	target_scale = 0.9

func take_damage(amount: int):
	cur_health -= amount
	OnTakeDamage.emit(cur_health)
	_play_audio(take_damage_sfx)
	
func heal (amount: int):
	cur_health += amount
	cur_health = clamp(cur_health, 0 , max_health)
	OnHeal.emit(cur_health)
	_play_audio(heal_sfx)
	
func cast_combat_action(action: CombatAction, opponent: Character):
	if action == null:
		return
	if action.melee_damage >0:
		opponent.take_damage(action.melee_damage)
	
	if action.heal_amount > 0:
		heal(action.heal_amount)
	
func _play_audio(stream: AudioStream):
	pass
