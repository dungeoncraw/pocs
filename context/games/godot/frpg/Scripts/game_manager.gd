extends Node
# this is a singleton, load from game not tied to any node

signal NewDay(day: int)
signal SetPlayerTool(tool: PlayerTools.Tool, seed: CropData)
signal HarversCrop(crop: Crop)
signal ChangeSeedQuantity(crop_data: CropData, quantity: int)
signal ChangeMoney(money: int)

var day: int = 1
var money: int = 0
var all_crop_data: Array[CropData] = [
	preload("res://Crops/tomato.tres"),
	preload("res://Crops/corn.tres")
]
var owned_seeds: Dictionary[CropData, int]

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	# call_deferred wait until all the nodes process in current tick to emit
	for cd in all_crop_data:
		give_seed.call_deferred(cd, 2)
	give_money.call_deferred(10)
	

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func set_next_day():
	day += 1
	NewDay.emit(day)
	
func harverst_crop(crop: Crop):
	give_money(crop.crop_data.sell_price)
	HarversCrop.emit(crop)
	crop.queue_free()
	
func try_buy_seed(crop_data: CropData):
	if money < crop_data.seed_price:
		return
	money -= crop_data.seed_price
	owned_seeds[crop_data] += 1
	ChangeMoney.emit(money)
	ChangeSeedQuantity.emit(crop_data, owned_seeds[crop_data])
	

func consume_seed(crop_data: CropData):
	owned_seeds[crop_data] -= 1
	ChangeSeedQuantity.emit(crop_data, owned_seeds[crop_data])
	
func give_money(amount: int):
	money += amount
	ChangeMoney.emit(money)

func give_seed(crop_data: CropData, amount: int):
	if owned_seeds.has(crop_data):
		owned_seeds[crop_data] += amount
	else:
		owned_seeds[crop_data] = amount
	
	ChangeSeedQuantity.emit(crop_data, owned_seeds[crop_data])
