class_name HealthBar
extends ProgressBar

@onready var text : Label = $HealthText
@onready var character : Character = get_parent()

func _ready ():
	max_value = character.max_health
	character.HealthUpdated.connect(_update_ui)
	
	_update_ui()

func _update_ui ():
	if not character:
		return
	
	value = character.current_health
	text.text = str(character.current_health, " / ", character.max_health)
