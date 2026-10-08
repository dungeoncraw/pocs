class_name CardManager
extends Node

signal CardPilesChanged (draw : int, discard : int)

@export var player_deck : Array[CardData]
@export var card_scene : PackedScene
@export var cards_to_deal : int = 3
@export var x_offset : float

var draw_pile : Array[CardData]
var discard_pile : Array[CardData]

var card_nodes : Array[Card]

@onready var card_origin : Node2D = $CardOrigin
@onready var card_spawn : Node2D = $CardSpawn

var game_manager : GameManager:
	get: return ManagerRegistry.get_manager("game_manager")

func _enter_tree():
	ManagerRegistry.register("card_manager", self)

func _exit_tree():
	ManagerRegistry.unregister("card_manager")

func _ready ():
	game_manager.TurnBegan.connect(_on_turn_began)
	game_manager.TurnEnded.connect(_on_turn_ended)
	
	# Populate the draw pile
	draw_pile = player_deck.duplicate()
	draw_pile.shuffle()
	CardPilesChanged.emit(len(draw_pile), len(discard_pile))

# Determine each card's position and rotation in the hand
func _rearrange_cards ():
	for i in len(card_nodes):
		var card : Card = card_nodes[i]
		card.idle_pos = _get_card_position(i)
		card.default_z_index = i + 1
		card.rotation_degrees = card.idle_pos.x * 0.1

# Called at the start of each round - give the player x number of cards
func _deal_hand ():
	for i in range(cards_to_deal):
		await get_tree().create_timer(0.2).timeout
		_deal_card()

# Remove card from draw pile and add it to the player's hand
func _deal_card ():
	# No more cards in draw pile?
	# Move from discard pile and shuffle
	if len(draw_pile) == 0:
		draw_pile = discard_pile.duplicate()
		draw_pile.shuffle()
		discard_pile.clear()
		CardPilesChanged.emit(len(draw_pile), len(discard_pile))
	
	# Remove card from draw pile
	var card_data : CardData = draw_pile.pop_back()
	CardPilesChanged.emit(len(draw_pile), len(discard_pile))
	
	# Spawn the card node
	var card : Card = card_scene.instantiate()
	add_child(card)
	card.global_position = card_spawn.global_position
	card_nodes.append(card)
	card.z_index = len(card_nodes)
	
	card.setup(card_data)
	_rearrange_cards()

# Where the given card should be positioned
func _get_card_position (card_index : int) -> Vector2:
	var total : float = len(card_nodes)
	var left_x : float = ((-total + 1) * x_offset) / 2.0
	var pos_x : float = left_x + (card_index * x_offset)
	
	return card_origin.global_position + Vector2(pos_x, 0)

# Called when a card is casted/for all remaining cards at end of round
# Delete the node and add the card to the discard pile
func discard_card (card : Card):
	# Add card to discard pile
	discard_pile.append(card.data)
	CardPilesChanged.emit(len(draw_pile), len(discard_pile))
	
	# Delete card node
	card_nodes.erase.call_deferred(card)
	card.queue_free()
	_rearrange_cards.call_deferred()

func _on_turn_began (character : Character):
	if character.is_player:
		_deal_hand()

func _on_turn_ended (character : Character):
	if character.is_player:
		for card in card_nodes:
			discard_card(card)
