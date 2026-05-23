class_name CinematicCamera
extends Camera3D

@export var id : String
@export var track_target : Node3D

@export var do_handheld : bool
@export var handheld_noise : FastNoiseLite
@export var handheld_amplitude : float
@export var handheld_frequency : float

var time : float

func _process (delta : float):
	if track_target != null:
		look_at(track_target.global_position)
	
	if not do_handheld:
		return
	
	time += delta
	
	var offset : float = time * handheld_frequency
	h_offset = handheld_noise.get_noise_2d(offset, 0) * handheld_amplitude
	v_offset = handheld_noise.get_noise_2d(0, offset) * handheld_amplitude
