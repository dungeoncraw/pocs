extends Area2D

var targets: Array

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func setup(pos: Vector2):
	position = pos


func _on_body_entered(body: Node2D) -> void:
	targets.append(body)

func hurt_targets():
	for target in targets:
		target.hit()
