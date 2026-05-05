extends Area2D

@export var speed: float = 200.0
@export var owner_group: String
@onready var destroy_timer: Timer = $DestroyTimer

var move_dir: Vector2

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	translate(move_dir * speed * delta)
	rotation = move_dir.angle()


func _on_body_entered(body: Node2D) -> void:
	pass # Replace with function body.


func _on_destroy_timer_timeout() -> void:
	visible = false


func _on_visibility_changed() -> void:
	if visible == true and destroy_timer:
		destroy_timer.start()
