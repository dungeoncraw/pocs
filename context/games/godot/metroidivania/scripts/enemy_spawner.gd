extends Marker2D

@export var type: Data.Enemy
var unique_id: String

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

# called before the ready function
func _enter_tree() -> void:
	unique_id = get_unique_id()
	
func get_unique_id() -> String:
	var scene_name = get_owner().scene_file_path.get_file().get_basename()
	return str(scene_name) + "_" + str(name)
