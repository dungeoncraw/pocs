extends Area2D
enum PotionType
{
	HEALTH,
	SHOOT_SPEED,
	MOVE_SPEED
}

@export var type: PotionType 
@export var value: float

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	var t = Time.get_unix_time_from_system()
	var s = 1 + (sin(t * 10) * 0.1)
	scale.x = s
	scale.y = s


func _on_body_entered(body: Node2D) -> void:
	if not body.is_in_group("Player"):
		return
		
	if type == PotionType.HEALTH:
		body.heal(value)
	elif type == PotionType.SHOOT_SPEED:
		body.shoot_rate *= value
		body.additional_bullet_speed += 30
	elif type == PotionType.MOVE_SPEED:
		body.max_speed *= value
	queue_free()
