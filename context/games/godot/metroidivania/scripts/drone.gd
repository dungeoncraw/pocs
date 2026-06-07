extends CharacterBody2D

var direction: Vector2
var speed: int= 50
var player: CharacterBody2D
var health: int = 3:
	set(value):
		health = value
		if health <= 0:
			explode.emit(position)
			queue_free()


signal explode(pos: Vector2)

func _on_p_layer_detection_area_body_entered(body: Node2D) -> void:
	player = body


func _on_p_layer_detection_area_body_exited(_body: Node2D) -> void:
	player = null

func _physics_process(delta: float) -> void:
	if player:
		# Vector between two bodies (player.position - position)
		var dir = (player.position - position).normalized()
		velocity = dir * speed
		move_and_slide()

func hit():
	health -= 1
	var tween = create_tween()
	tween.tween_property($AnimatedSprite2D.material, 'shader_parameter/Progress', 1.0, 0.3)
	tween.tween_property($AnimatedSprite2D.material, 'shader_parameter/Progress', 0.0, 0.5)


func _on_collision_shape_body_entered(body: Node2D) -> void:
	explode.emit(position)
	queue_free()
