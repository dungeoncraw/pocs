extends Area2D

var path_follow: PathFollow2D

func setup(new_path_follow: PathFollow2D):
	path_follow = new_path_follow

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	path_follow.progress += 20 * delta
	if path_follow.progress_ratio >= 0.99:
		queue_free()
