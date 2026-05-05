extends Node

@export var node_scene: PackedScene
var cached_nodes: Array[Node2D]


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _create_new() -> Node2D:
	var node = node_scene.instantiate()
	cached_nodes.append(node)
	get_tree().get_root().add_child(node)
	return node
	
	
func spawn() -> Node2D:
	for node in cached_nodes:
		if node.visible == false:
			node.visible = true
			return node
	return _create_new()
