extends CanvasLayer

@onready var health_container: HBoxContainer = $HealthContainer
var hearts: Array = []
@onready var score_text: Label = $ScoreText
# get_parent get the current parent in tree
@onready var player = get_parent()

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	# pickup the hearts in container
	hearts = health_container.get_children()
	
	player.OnUpdateHealth.connect(_update_hearts)
	player.OnUpdateScore.connect(_update_score)
	
	_update_hearts(player.health)
	_update_score(PlayerStats.score)

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _update_hearts(health: int):
	for i in len(hearts):
		hearts[i].visible = i < health
func _update_score(score: int):
	score_text.text = "Score: " + str(score)
