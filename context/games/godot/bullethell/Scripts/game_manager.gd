extends Node2D

var elapsed_time: float

@onready var elapsed_time_text: Label = $CanvasLayer/ElapsedTimeText
@onready var end_screen: Panel = $CanvasLayer/EndScreen
@onready var end_text: Label = $CanvasLayer/EndScreen/EndText
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	elapsed_time += delta
	elapsed_time_text.text = str("%.1f" % elapsed_time)

func set_game_over():
	# pause engine
	Engine.time_scale = 0.0
	end_screen.visible = true
	end_text.text = "You survived for " + str("%.1f" % elapsed_time) + " seconds"

func _on_retry_button_pressed() -> void:
	# start engine
	Engine.time_scale = 1.0
	get_tree().reload_current_scene()
