class_name Card
extends Area2D

enum State 
{
	IDLE,
	HOVERED,
	DRAGGING
}

@export var data: CardData

@onready var name_label: Label = $DisplayNameLabel
@onready var cost_label: Label = $CostLabel
@onready var description_label: Label = $DescriptionLabel
@onready var icon_rect: TextureRect = $Icon

var state: State = State.IDLE
var idle_pos: Vector2
var hover_pos: Vector2:
	get: return idle_pos + Vector2(0, -30)

func _setup_visual():
	name_label.text = data.display_name
	cost_label.text = str(data.cost)
	description_label.text = data.get_description()
	icon_rect.texture = data.icon
# Called when the node enters the scene tree for the first time.
func setup(data: CardData) -> void:
	self.data = data
	state = State.IDLE
	_setup_visual()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	var lerp_speed: float = 10
	var target_position: Vector2 = idle_pos
	
	if state == State.HOVERED:
		target_position = hover_pos
		lerp_speed  = 15
	elif state == State.DRAGGING:
		target_position = get_global_mouse_position()
		lerp_speed = 20
		
	global_position = global_position.lerp(target_position, delta * lerp_speed)
	
func hover_enter():
	state = State.HOVERED
	
func hover_exit():
	state = State.IDLE

func begin_drag():
	state = State.DRAGGING

func end_drag():
	state = State.IDLE
