extends Area3D
#make available in the inspector
@export var clicks_to_pop: int = 5
@export var size_increase: float = 0.2
@export var score_to_give: int = 1

#reference to balloon_manager script
var manager

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	manager = $".."


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass


func _on_input_event(camera: Node, event: InputEvent, event_position: Vector3, normal: Vector3, shape_idx: int) -> void:
	if event is not InputEventMouseButton:
		return
	
	if event.button_index!= MOUSE_BUTTON_LEFT:
		return
	
	if not event.is_pressed():
		return

	# Vector3.ONE generates a Vector3(1,1,1) so multiplying by size_increase do the magi	
	scale += Vector3.ONE * size_increase
	clicks_to_pop -= 1
	
	if clicks_to_pop == 0:
		manager.increase_score(score_to_give)
		#destroy current node
		queue_free()
