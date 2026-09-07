class_name HealthBar
extends ProgressBar

@onready var text: Label = $HealthText
@onready var character: Character = get_parent()

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	max_value = character.max_health
	character.HealthUpdated.connect(_update_ui)
	
	_update_ui()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _update_ui():
	if not character:
		return

	value = character.current_health
	text.text = str(character.current_health, " / ", character.max_health)
