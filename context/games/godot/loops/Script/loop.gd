extends Node2D


var start_scene: PackedScene = preload("res://Scene/start.tscn")
@export var spawn_count: int = 200
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	for i in spawn_count:
		var star: Node2D = start_scene.instantiate()
		add_child(star)
		# randf_range generate random float values in range
		star.position.x = randf_range(-280, 280)
		star.position.y = randf_range(-150, 150)
		
		var star_scale = randf_range(0.5, 1.2)
		star.scale.x = star_scale
		star.scale.y = star_scale

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
