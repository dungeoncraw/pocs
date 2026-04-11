extends AudioStreamPlayer

@export var take_damage_sfx: AudioStream

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	var unit: Unit = get_parent()
	unit.OnTakeDamage.connect(_play_take_damage_sfx)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _play_take_damage_sfx(health: int):
	_play_sound(take_damage_sfx)

func _play_sound(audio: AudioStream):
	stream = audio
	play()
