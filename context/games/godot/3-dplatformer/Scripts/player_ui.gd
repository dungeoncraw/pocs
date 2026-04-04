extends CanvasLayer

@onready var health_container: HBoxContainer = $HealthContainer
@onready var score_text: Label = $ScoreText

var hearts: Array[Node] = []

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	_update_score_text(0)
	#	get children nodes
	hearts = health_container.get_children()
	var player = get_parent()
	player.OnTakeDamage.connect(_update_hearts)
	player.OnUpdateScore.connect(_update_score_text)

# should match the parameters for the signal
func _update_hearts(health: int):
	for i in len(hearts):
		hearts[i].visible = i < health

func _update_score_text(score: int):
	score_text.text = "Score: " + str(score)
