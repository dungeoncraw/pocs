extends Control

## Home screen controller for "Pantheon // Override".
## Builds the frozen bullet ring around the title and wires up the menu.

@onready var bullet_ring: Node2D = $BulletRing
@onready var btn_play: Button = $ContentMargin/ContentVBox/Menu/BtnPlay
@onready var btn_load: Button = $ContentMargin/ContentVBox/Menu/BtnLoad
@onready var btn_settings: Button = $ContentMargin/ContentVBox/Menu/BtnSettings
@onready var btn_quit: Button = $ContentMargin/ContentVBox/Menu/BtnQuit

const RING_COUNT := 22
const RING_RADIUS := 240.0

const TECH_COLOR := Color(0.184, 0.965, 1.0, 0.55)
const GOLD_COLOR := Color(0.863, 0.682, 0.337, 0.55)


func _ready() -> void:
	_build_bullet_ring()

	btn_play.pressed.connect(_on_play_pressed)
	btn_load.pressed.connect(_on_load_pressed)
	btn_settings.pressed.connect(_on_settings_pressed)
	btn_quit.pressed.connect(_on_quit_pressed)


func _build_bullet_ring() -> void:
	for i in range(RING_COUNT):
		var angle := (float(i) / float(RING_COUNT)) * TAU
		var is_tech := i % 2 == 0

		var bullet := ColorRect.new()
		var size := 5.0 if is_tech else 6.0
		bullet.size = Vector2(size, size)
		bullet.color = TECH_COLOR if is_tech else GOLD_COLOR

		var pos := Vector2(cos(angle), sin(angle)) * RING_RADIUS
		bullet.position = pos - bullet.size * 0.5

		if is_tech:
			# diamond orientation for the tech half, matching the HTML version
			bullet.pivot_offset = bullet.size * 0.5
			bullet.rotation = deg_to_rad(45)

		bullet_ring.add_child(bullet)


func _on_play_pressed() -> void:
	# TODO: point this at your gameplay scene, e.g.:
	# get_tree().change_scene_to_file("res://scenes/game/game.tscn")
	print("Play pressed")


func _on_load_pressed() -> void:
	# TODO: open your load/save UI
	print("Load pressed")


func _on_settings_pressed() -> void:
	# TODO: open your settings menu
	print("Settings pressed")


func _on_quit_pressed() -> void:
	get_tree().quit()
