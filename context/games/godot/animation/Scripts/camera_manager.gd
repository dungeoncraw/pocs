class_name CameraManager
extends Node

@export var cameras : Array[CinematicCamera]

func activate_camera (camera_id : String):
	for cam in cameras:
		if cam.id == camera_id:
			cam.current = true
