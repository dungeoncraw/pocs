class_name CharacterAnimation
extends AnimatedSprite2D

const IDLE_ANIM: String = "idle"
const ATTACK_ANIM: String = "attack"
const HIT_ANIM: String = "hit"
const DEATH_ANIM: String = "death"

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_animation_finished() -> void:
	if animation == IDLE_ANIM:
		return
	if animation == DEATH_ANIM:
		return
	play(IDLE_ANIM)
