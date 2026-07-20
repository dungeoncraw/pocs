class_name Tower extends Node2D

var enemies: Array
var type: Data.Tower
var upgraded: bool
var bullet_type: Data.Bullet
var cost: int
var upgrade_cost: int
@warning_ignore("unused_signal")
signal shoot(pos: Vector2, direction: float, bullet_enum: Data.Bullet)
signal select(tower: Tower)

func setup(tower_type: Data.Tower):
	$ReloadTimer.wait_time = Data.TOWER_DATA[tower_type]['reload_time']
	$TowerMenu.cost = Data.TOWER_DATA[tower_type]['upgrade_cost']
	bullet_type = Data.TOWER_DATA[tower_type]['bullet']
	cost = Data.TOWER_DATA[tower_type]['cost']
	upgrade_cost = Data.TOWER_DATA[tower_type]['upgrade_cost']
	type = tower_type


func _on_enemy_detection_area_area_entered(area: Area2D) -> void:
	if area not in enemies:
		enemies.append(area)


func _on_enemy_detection_area_area_exited(area: Area2D) -> void:
	if area in enemies:
		enemies.erase(area)


func _on_click_area_input_event(_viewport: Node, event: InputEvent, _shape_idx: int) -> void:
	if event is InputEventMouseButton and event.button_index == 1 and event.button_mask == 1:
		if not $DelayTimer.time_left:
			select.emit(self)
			$TowerMenu.reveal(upgraded)


func _on_tower_menu_upgrade_press() -> void:
	Data.money -= Data.TOWER_DATA[type]['upgrade_cost']
	tower_upgrade()
	$TowerMenu.hide()
	upgraded = true


func tower_upgrade():
	pass


func _on_tower_menu_delete_press() -> void:
	var return_money = cost if not upgraded else cost + upgrade_cost
	Data.money += return_money
	queue_free()


func hide_ui():
	$TowerMenu.hide()
