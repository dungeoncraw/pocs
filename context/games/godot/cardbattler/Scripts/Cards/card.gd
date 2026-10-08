class_name Card
extends Area2D

enum State
{
	IDLE,
	HOVERED,
	DRAGGING
}

@export var data : CardData

@onready var name_label : Label = $DisplayNameLabel
@onready var cost_label : Label = $CostLabel
@onready var description_label : Label = $DescriptionLabel
@onready var icon_rect : TextureRect = $Icon

@export var hover_sfx : AudioStream
@export var drag_sfx : AudioStream

var state : State

var idle_pos : Vector2
var hover_pos : Vector2:
	get: return idle_pos + Vector2(0, -30)

var default_z_index : int

var game_manager : GameManager:
	get: return ManagerRegistry.get_manager("game_manager")

var audio_manager : AudioManager:
	get: return ManagerRegistry.get_manager("audio_manager")

func _ready ():
	game_manager.ManaChanged.connect(_on_mana_changed)

func setup (data : CardData):
	self.data = data
	state = State.IDLE
	_setup_visual()

func cast ():
	var cast_data : CardData.CastData = CardData.CastData.new()
	cast_data.caster = game_manager.player
	cast_data.opponent = game_manager.enemy
	
	data.cast(cast_data)

# Each frame we want to move the card to a position depending on state
func _process (delta : float):
	var lerp_speed : float = 10
	
	# By default, the target pos is the idle pos down in the player's hand
	var target_pos : Vector2 = idle_pos
	
	# Hovering - move up 30 pixels from idle pos
	if state == State.HOVERED:
		target_pos = hover_pos
		lerp_speed = 15
	# Dragging - move towards mouse position
	elif state == State.DRAGGING:
		target_pos = get_global_mouse_position()
		lerp_speed = 20
	
	global_position = global_position.lerp(target_pos, delta * lerp_speed)

# Called when spawned - apply CardData info to the UI
func _setup_visual ():
	name_label.text = data.display_name
	cost_label.text = str(data.cost)
	description_label.text = data.get_description()
	icon_rect.texture = data.icon

func hover_enter ():
	state = State.HOVERED
	z_index = 99
	
	audio_manager.play(hover_sfx)

func hover_exit ():
	state = State.IDLE
	z_index = default_z_index

func begin_drag ():
	state = State.DRAGGING
	
	audio_manager.play(drag_sfx)

func end_drag ():
	state = State.IDLE

func _on_mana_changed (cur : int, max : int):
	if cur >= data.cost:
		cost_label.self_modulate = Color.WHITE
	else:
		cost_label.self_modulate = Color.RED
