extends Area2D

var rotate_speed: float = 3.0
var bob_height: float = 5.0
var bob_speed: float = 5.0

@onready var start_pos: Vector2 = global_position
@onready var sprite: Sprite2D = $Sprite
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _physics_process(delta: float) -> void:
	var time = Time.get_unix_time_from_system()
	
	#rotate
	sprite.scale.x = sin(time * rotate_speed)
	
	#bob
	var y_pos = ((1 + sin(time * bob_speed)) / 2) * bob_height
	global_position.y = start_pos.y - y_pos


func _on_body_entered(body: Node2D) -> void:
	if not body.is_in_group("Player"):
		return
	body.increase_score(1)
	queue_free()
	pass # Replace with function body.
