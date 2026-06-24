extends Area2D
@export var target: Data.Level

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_body_entered(player: CharacterBody2D) -> void:
	player.freeze()
	TransitionLayer.transition(target, Data.current_level)
