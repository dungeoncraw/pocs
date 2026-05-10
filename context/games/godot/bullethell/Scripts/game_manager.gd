extends Node2D

var elapsed_time: float

@onready var elapsed_time_text: Label = $CanvasLayer/ElapsedTimeText

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	elapsed_time += delta
	elapsed_time_text.text = str("%.1f" % elapsed_time)
