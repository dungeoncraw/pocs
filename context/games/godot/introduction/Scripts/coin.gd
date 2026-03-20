extends Area2D


func _on_body_entered(body: Node2D) -> void:
	#	the body is the reference of who colided, so in this case the player
	body.scale.x += 0.3
	body.scale.y += 0.3
	#	destroy current node
	queue_free()
