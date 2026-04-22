extends AudioStreamPlayer3D

@export var footstep_sounds: Array[AudioStream]
@export var play_rate: float = 0.5
var last_play_time: float
@export var min_velocity: float = 0.5

@onready var player: CharacterBody3D = get_parent()


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if not player.is_on_floor():
		return
	
	if player.velocity.length() < min_velocity:
		return
	
	if Time.get_unix_time_from_system() - last_play_time < play_rate:
		return
	
	last_play_time = Time.get_unix_time_from_system()
	stream = footstep_sounds[randi() % len(footstep_sounds)]
	play()
