class_name Crop

extends Node2D

var crop_data: CropData
var days_until_grown: int
var watered: bool
var harvestable: bool
var tile_map_coords: Vector2i

@onready var sprite: Sprite2D = $Sprite
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func set_crop(data: CropData, already_watered: bool, tile_coords: Vector2i):
	crop_data = data
	watered = already_watered
	tile_map_coords = tile_coords
	
	harvestable = false
	days_until_grown = crop_data.days_to_growth
	sprite.texture = crop_data.growth_sprites[0]
	
func _on_new_day(day: int):
	pass
