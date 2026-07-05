extends Node2D

var enemy_scene = preload("res://scenes/enemies/enemy.tscn")

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	var path_follow = PathFollow2D.new()
	var enemy = enemy_scene.instantiate()
	enemy.setup(path_follow)
	path_follow.add_child(enemy)
	$Path2D.add_child(path_follow)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
