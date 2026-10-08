class_name CharacterAnimation
extends AnimatedSprite2D

const IDLE_ANIM : String = "idle"
const ATTACK_ANIM : String = "attack"
const HIT_ANIM : String = "hit"
const DEATH_ANIM : String = "death"

# When an animation finishes playing, we want to return
# to the idle animation instead of freezing at the final frame
# Don't return to idle if finished animation is IDLE or DEATH
func _on_animation_finished():
	if animation == IDLE_ANIM:
		return
	
	if animation == DEATH_ANIM:
		return
	
	play(IDLE_ANIM)
