class_name Character

extends Node2D

signal OnTakeDamage(health: int)
signal OnHeal(health: int)


@export var is_player: bool
@export var cur_health: int
@export var max_health: int

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
	
func end_turn():
	target_scale = 0.9

func take_damage(amount: int):
	pass
	
func heal (amount: int):
	pass
	
func cast_combat_action(action, opponent: Character):
	pass
	
func _play_audio(stream: AudioStream):
	pass
