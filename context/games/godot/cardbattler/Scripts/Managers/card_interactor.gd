extends Node2D

var selected_card : Card
var mouse_down_last_frame : bool

var card_manager : CardManager:
	get: return ManagerRegistry.get_manager("card_manager")

var game_manager : GameManager:
	get: return ManagerRegistry.get_manager("game_manager")

func _process (delta : float):
	var cur_hover_card : Card = _get_selected_card()
	var mouse_down : bool = Input.is_mouse_button_pressed(MOUSE_BUTTON_LEFT)
	
	# Only select a card when mouse is released
	if not mouse_down:
		# Are we hovering over a card?
		if cur_hover_card != null:
			# Is this card not our currently selected?
			if selected_card != null and cur_hover_card != selected_card:
				selected_card.hover_exit()
				selected_card = null
			
			# Select the card
			if cur_hover_card != selected_card:
				selected_card = cur_hover_card
				selected_card.hover_enter()
		# Are we hovering over nothing?
		elif selected_card != null:
			selected_card.hover_exit()
			selected_card = null
	
	if selected_card != null:
		# Mouse down this frame
		if mouse_down and not mouse_down_last_frame:
			_pickup_card()
		# Mouse released this frame
		elif not mouse_down and mouse_down_last_frame:
			_drop_card()
	
	mouse_down_last_frame = mouse_down

func _pickup_card ():
	if not selected_card:
		return
	
	selected_card.begin_drag()

func _drop_card ():
	if not selected_card:
		return
	
	selected_card.end_drag()
	
	# Don't cast if the card is below the characters
	if selected_card.global_position.y > 20:
		return
	
	# Don't cast if we're lacking mana
	if game_manager.current_mana < selected_card.data.cost:
		return
	
	selected_card.cast()
	game_manager.spend_mana(selected_card.data.cost)
	card_manager.discard_card(selected_card)

func _get_selected_card () -> Card:
	var mouse_pos : Vector2 = get_global_mouse_position()
	var space_state = get_world_2d().direct_space_state
	
	# Query defines where and what we want to interact with
	var query = PhysicsPointQueryParameters2D.new()
	query.position = mouse_pos
	query.collide_with_areas = true
	
	# Intersect at our mouse and fetch all areas we hit
	var intersections = space_state.intersect_point(query)
	
	# Keep track of the top-most card
	var card : Card = null
	var card_z_index : int = -1
	
	# Find the top-most card of those selcted
	for result in intersections:
		var hit = result["collider"]
		
		if hit is Card and hit.z_index > card_z_index:
			card = hit
			card_z_index = hit.z_index
	
	return card
