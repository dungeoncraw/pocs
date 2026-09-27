class_name CardManager
extends Node

signal CardPileChanged (draw: int, discard: int)

@export var player_deck: Array[CardData]
@export var card_scene: PackedScene
@export var cards_to_deal: int = 3
@export var x_offset: float

var draw_pile: Array[CardData]
var discard_pile: Array[CardData]

var card_nodes: Array[Card]

@onready var card_origin: Node2D = $CardOrigin
@onready var card_spawn: Node2D = $CardSpawn


func _enter_tree() -> void:
	ManagerRegistry.register("card_manager", self)
	
func _exit_tree() -> void:
	ManagerRegistry.unregister("card_manager")

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	draw_pile = player_deck.duplicate()
	draw_pile.shuffle()
	CardPileChanged.emit(len(draw_pile), len(discard_pile))


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _rearrange_cards():
	for i in len(card_nodes):
		var card: Card = card_nodes[i]
		card.idle_pos = _get_card_position(i)
		card.default_z_index = i + 1
		

func _deal_hand():
	for i in range(cards_to_deal):
		await get_tree().create_timer(0.2).timeout
		_deal_card()
	
func _deal_card():
	if len(draw_pile) == 0:
		draw_pile = discard_pile.duplicate()
		draw_pile.shuffle()
		discard_pile.clear()
		CardPileChanged.emit(len(draw_pile), len(discard_pile))
	
	var card_data: CardData = draw_pile.pop_back()
	CardPileChanged.emit(len(draw_pile), len(discard_pile))
	
	var card: Card = card_scene.instantiate()
	add_child(card)
	card.global_position = card_spawn.global_position
	card_nodes.append(card)
	card.z_index = len(card_nodes)
	
	card.setup(card_data)
	_rearrange_cards()
	
func _get_card_position(card_index: int) -> Vector2:
	var total: int = len(card_nodes)
	var left_x: float = ((-total + 1) * x_offset) / 2.0
	var pos_x: float = left_x + (card_index * x_offset)
	return card_origin.global_position + Vector2(pos_x, 0)

func discard_card(card: Card):
	discard_pile.append(card.data)
	CardPileChanged.emit(len(draw_pile), len(discard_pile))
	
	card_nodes.erase(card)
	card.queue_free()
	_rearrange_cards()
