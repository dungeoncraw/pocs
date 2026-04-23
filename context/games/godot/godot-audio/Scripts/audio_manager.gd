extends Node

@onready var window: Panel = get_node("AudioWindow")
@onready var master_slide: HSlider = get_node("AudioWindow/MasterVolumeSlider")
@onready var sfx_slide: HSlider = get_node("AudioWindow/SFXVolumeSlider")
@onready var music_slide: HSlider = get_node("AudioWindow/MusicVolumeSlider")

var master_index: int
var sfx_index: int
var music_index: int

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	master_index = AudioServer.get_bus_index("Master")
	sfx_index = AudioServer.get_bus_index("SFX")
	music_index = AudioServer.get_bus_index("Music")
	master_slide.value = _get_volume(master_index)
	sfx_slide.value = _get_volume(sfx_index)
	music_slide.value = _get_volume(music_index)

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if Input.is_action_just_pressed("ui_cancel"):
		window.visible = !window.visible
	if window.visible:
		Input.mouse_mode = Input.MOUSE_MODE_VISIBLE
	else:
		Input.mouse_mode = Input.MOUSE_MODE_CAPTURED
	
func _get_volume(bus_index) -> float:
	var db_volume = AudioServer.get_bus_volume_db(bus_index)
	return db_to_linear(db_volume)

func _set_volume(bus_index: int, volume: float):
	var db_volume = linear_to_db(volume)
	AudioServer.set_bus_volume_db(bus_index, db_volume)

#decibels move on logarithm step


func _on_master_volume_slider_value_changed(value: float) -> void:
	_set_volume(master_index, value)


func _on_sfx_volume_slider_value_changed(value: float) -> void:
	_set_volume(sfx_index, value)


func _on_music_volume_slider_value_changed(value: float) -> void:
	_set_volume(music_index, value)
