extends CanvasLayer
signal place_tower(tower_type: Data.Tower)
signal start_wave
enum MenuState {CLOSED, OPEN}
var current_state: MenuState = MenuState.CLOSED
const MENU_BUTTON_TEXTURES = {
	MenuState.CLOSED: {
		'normal': "res://graphics/ui/menu.png",
		'pressed':"res://graphics/ui/menu.png",
		'hover': "res://graphics/ui/menu_hover.png"},
	MenuState.OPEN: {
		'normal': "res://graphics/ui/close_normal.png",
		'pressed': "res://graphics/ui/close_normal.png",
		'hover': "res://graphics/ui/close_hover.png"}}
var tower_card_scene = preload("res://scenes/ui/tower_card.tscn")


func _ready() -> void:
	change_button_texture(current_state)
	$TowerCards/TowerCardsContainer.visible = current_state == MenuState.OPEN
	for tower_enum in Data.Tower.values():
		var tower_card = tower_card_scene.instantiate()
		tower_card.setup(tower_enum)
		$TowerCards/TowerCardsContainer.add_child(tower_card)
		tower_card.connect('press', tower_select)


func tower_select(tower_enum: Data.Tower):
	place_tower.emit(tower_enum)


func update_stats(money: int, health: int):
	$Control/StatsContainer/PanelContainer2/HBoxContainer/Label.text = str(money)
	$Control/StatsContainer/PanelContainer/HBoxContainer/Label.text = str(health)


func _on_wave_button_pressed() -> void:
	start_wave.emit()


func change_button_texture(state: MenuState):
	$TowerCards/MenuToggleButton.texture_normal = load(MENU_BUTTON_TEXTURES[state]['normal'])
	$TowerCards/MenuToggleButton.texture_hover = load(MENU_BUTTON_TEXTURES[state]['hover'])
	$TowerCards/MenuToggleButton.texture_pressed = load(MENU_BUTTON_TEXTURES[state]['pressed'])


func _on_menu_toggle_button_pressed() -> void:
	current_state = MenuState.CLOSED if current_state == MenuState.OPEN else MenuState.OPEN
	change_button_texture(current_state)
	$TowerCards/TowerCardsContainer.visible = current_state == MenuState.OPEN


func hide_cards():
	current_state = MenuState.CLOSED
	change_button_texture(current_state)
	$TowerCards/TowerCardsContainer.visible = current_state == MenuState.OPEN
