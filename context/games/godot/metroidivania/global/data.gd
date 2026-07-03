extends Node

enum Gun {SINGLE, SHOTGUN, ROCKET}
enum Level {SUBWAY, SKY, SEWER}
enum Enemy {DRONE, SOLDIER}
const LEVEL_PATHS = {
	Level.SUBWAY: "res://scenes/levels/subway.tscn",
	Level.SKY: "res://scenes/levels/sky.tscn",
	Level.SEWER: "res://scenes/levels/sewer.tscn",
}

var current_level: Level = Level.SUBWAY
var player_health: int = 5
var enemy_data: Dictionary
