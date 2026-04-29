class_name PlayerTools
extends Node2D

enum Tool
{
	HOE,
	WATER_BUCKET,
	SCYTHE,
	SEED
}

var current_tool: Tool
var current_seed: CropData

@onready var farm_manager: FarmManager = $"../../FarmManager"

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	current_tool = Tool.HOE


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	if Input.is_action_just_pressed("interact"):
		match current_tool:
			Tool.HOE:
				farm_manager.try_till_tile(global_position)
			Tool.WATER_BUCKET:
				farm_manager.try_water_tile(global_position)
			Tool.SCYTHE:
				farm_manager.try_harvest_tile(global_position)
			Tool.SEED:
				farm_manager.try_seed_tile(global_position, current_seed)

func _set_tool(tool: Tool, seed: CropData):
	current_tool = tool
	current_seed = seed
