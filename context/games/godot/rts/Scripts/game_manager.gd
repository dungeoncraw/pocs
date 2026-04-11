extends Node2D

# dictionary
var units = {
	Unit.Team.PLAYER: 0,
	Unit.Team.AI: 0
}

@onready var end_screen = $CanvasLayer/EndScreen

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	for unit in get_tree().get_nodes_in_group("Unit"):
		if unit is not Unit:
			continue
		units[unit.team] += 1
		unit.OnDie.connect(_on_unit_die)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	pass

func _on_unit_die(unit: Unit):
	units[unit.team] -= 1
	_check_win_condition()
	
func _check_win_condition():
	var winner = 0
	var teams_alive = 0
	for team in units:
		if units[team] > 0:
			teams_alive += 1
			winner = team
	if teams_alive > 1:
		return
		
	var team_name = Unit.Team.keys()[winner]
	
	end_screen._set_screen(team_name)
