extends Control

var cost: int = 100
signal upgrade_press
signal delete_press


func _ready() -> void:
	toggle_active(Data.money)
	$UpgradeButton.text = 'Upgrade (' + str(cost) + ')'

func _on_upgrade_button_pressed() -> void:
	upgrade_press.emit()


func reveal(upgraded: bool):
	show()
	if upgraded:
		$UpgradeButton.hide()

func toggle_active(money: int):
	$UpgradeButton.disabled = cost > money


func _on_delete_button_pressed() -> void:
	delete_press.emit()
