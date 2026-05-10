extends Node

@export var potion_scenes: Array[PackedScene]
@export var min_bounds: Vector2
@export var max_bounds: Vector2

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_spawn_timer_timeout() -> void:
	var potion = potion_scenes[randi() % len(potion_scenes)].instantiate()
	add_child(potion)
	
	var spawn_x = randf_range(min_bounds.x, max_bounds.x)
	var spawn_y = randf_range(min_bounds.y, max_bounds.y)
	
	potion.global_position = Vector2(spawn_x, spawn_y)
