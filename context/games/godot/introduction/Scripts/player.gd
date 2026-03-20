extends CharacterBody2D


const SPEED = 110.0


func _physics_process(delta: float) -> void:
	#	avoid to stack each frame, so need to reset the velocity
	velocity = Vector2(0,0)
	
	if Input.is_key_pressed(KEY_RIGHT):
		velocity.x += SPEED
	if Input.is_key_pressed(KEY_LEFT):
		velocity.x -=SPEED
	if Input.is_key_pressed(KEY_UP):
		velocity.y -= SPEED
	if Input.is_key_pressed(KEY_DOWN):
		velocity.y +=SPEED
	
	move_and_slide()
