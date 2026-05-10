extends Area2D

@export var speed: float = 200.0
@export var owner_group: String
@onready var destroy_timer: Timer = $DestroyTimer
var additional_speed: float = 0
var move_dir: Vector2

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	translate(move_dir * (speed + additional_speed) * delta)
	rotation = move_dir.angle()


func _on_body_entered(body: Node2D) -> void:
	if body.is_in_group(owner_group):
		return
		
	if body.has_method("take_damage"):
		body.take_damage(1)
	visible = false

func _on_destroy_timer_timeout() -> void:
	visible = false


func _on_visibility_changed() -> void:
	if visible == true and destroy_timer:
		destroy_timer.start()
		additional_speed = 0
