extends ProgressBar

@onready var unit: Unit = get_parent()

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	max_value = unit.max_hp
	value = max_value
	visible = false
	unit.OnTakeDamage.connect(_update_value)

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _update_value(health:int):
	value = health
	# check if the value is less than property max_value 
	visible = value < max_value
