extends Sprite2D


func setup(pos):
	position = pos
	$AnimationPlayer.play("explosion")


func hit_enemies():
	for enemy in get_tree().get_nodes_in_group('Enemies'):
		if position.distance_to(enemy.global_position) < 30:
			enemy.hit()
