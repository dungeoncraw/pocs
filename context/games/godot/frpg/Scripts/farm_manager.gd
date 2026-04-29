class_name FarmManager

extends Node

enum TileType 
{
	GRASS,
	TILLED,
	TILLED_WATERED
}

class TileInfo:
	var tilled: bool
	var watered: bool
	var crop: Crop

@onready var tile_map: TileMapLayer = $FarmTileMap
var tile_info: Dictionary[Vector2i, TileInfo]
var crop_scene: PackedScene = preload("res://Scenes/crop.tscn")

# map to tile map coordinates
var tile_atlas_coords: Dictionary[TileType, Vector2i] = {
	TileType.GRASS: Vector2i(0,0),
	TileType.TILLED: Vector2i(1,0),
	TileType.TILLED_WATERED: Vector2i(0,1)
}

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	# connect signals
	
	# map all tiles containing a sprite to set the tile info
	for cell in tile_map.get_used_cells():
		tile_info[cell] = TileInfo.new()
	


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _on_new_day(day: int):
	pass
	
func _on_harvest_crop(crop: Crop):
	pass
	
func try_till_tile(player_pos: Vector2):
	# this returns the tile map coordinate based on vector position
	var coords: Vector2i = tile_map.local_to_map(player_pos)
	
	if tile_info[coords].crop:
		return
	if tile_info[coords].tilled:
		return
	_set_tile_state(coords, TileType.TILLED)

func try_water_tile(player_pos: Vector2):
	var coords: Vector2i = tile_map.local_to_map(player_pos)
	
	if not tile_info[coords].tilled:
		return
	
	_set_tile_state(coords, TileType.TILLED_WATERED)
	
	if tile_info[coords].crop:
		tile_info[coords].crop.watered = true

func try_seed_tile(player_pos: Vector2, crop_data: CropData):
	var coords: Vector2i = tile_map.local_to_map(player_pos)
	
	if not tile_info[coords].tilled:
		return
	if tile_info[coords].crop:
		return
	
	var crop: Crop = crop_scene.instantiate()
	add_child(crop)
	# now get a tile pos and convert to global_position Vector2
	crop.global_position = tile_map.map_to_local(coords)
	crop.set_crop(crop_data, is_tile_watered(coords), coords)
	tile_info[coords].crop = crop
	

func try_harvest_tile(player_pos: Vector2):
	var coords: Vector2i = tile_map.local_to_map(player_pos)
	if not tile_info[coords].crop:
		return
	if not tile_info[coords].crop.harvestable:
		return
	
	tile_info[coords].crop = null

func is_tile_watered(player_pos: Vector2) -> bool:
	return false
	
func _set_tile_state(coords: Vector2i, tile_type: TileType):
	tile_map.set_cell(coords, 0, tile_atlas_coords[tile_type])
	match tile_type:
		TileType.GRASS:
			tile_info[coords].tilled = false
			tile_info[coords].watered = false
		TileType.TILLED:
			tile_info[coords].tilled = true
			tile_info[coords].watered = false
		TileType.TILLED_WATERED:
			tile_info[coords].tilled = true
			tile_info[coords].watered = true
