class_name CameraController
extends Camera2D

var shake_intensity : float = 0

func _enter_tree():
	ManagerRegistry.register("camera_controller", self)

func _exit_tree():
	ManagerRegistry.unregister("camera_controller")

func shake (intensity : float):
	shake_intensity = intensity

func _process (delta : float):
	if shake_intensity <= 0.0:
		return
	
	shake_intensity = lerpf(shake_intensity, 0.0, delta * 10)
	offset.x = randf_range(-shake_intensity, shake_intensity)
	offset.y = randf_range(-shake_intensity, shake_intensity)
