extends Node

var managers: Dictionary[String, Node]

func register(manager_name: String, instance: Node):
	managers[manager_name] = instance
	
func unregister(manager_name: String):
	managers.erase(manager_name)

func has_manager(manager_name: String) -> bool:
	return managers.has(manager_name)
	
func get_manager(manager_name: String) -> Node:
	if not has_manager(manager_name):
		printerr(str(manager_name, " manager has not found"))
		return null
	return managers[manager_name]

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass
