extends Node2D

var selected_card: Card

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	var cur_hover_card: Card = _get_selected_card()
	var mouse_down: bool = Input.is_mouse_button_pressed(MOUSE_BUTTON_LEFT)
	
	if not mouse_down:
		if cur_hover_card != null and cur_hover_card != selected_card:
			selected_card = cur_hover_card
			selected_card.hover_enter()

func _get_selected_card() -> Card:
	var mouse_pos: Vector2 = get_global_mouse_position()
	var space_state = get_world_2d().direct_space_state
	
	var query = PhysicsPointQueryParameters2D.new()
	query.position = mouse_pos
	query.collide_with_areas = true
	
	var intersections = space_state.intersect_point(query)
	
	var card: Card = null
	var card_z_index: int = -1
	
	for result in intersections:
		var hit = result["collider"]
		
		if hit is Card and hit.z_index > card_z_index:
			card = hit
			card_z_index = hit.z_index
	
	return card
