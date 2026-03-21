extends Node3D

var score: int = 0
var score_text: Label

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	score_text = $ScoreText
	score_text.text = "Score: 0"
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func increase_score(amount: int):
	score += amount
	score_text.text = "Score: " + str(score)
