extends Node

enum GUN {
	SINGLE,
	SHOTGUN,
	ROCKET
}

enum Level {
	SUBWAY,
	SKY
}

const LEVEL_PATHS = {
	Level.SUBWAY: "res://scenes/levels/subway.tscn",
	Level.SKY: "res://scenes/levels/sky.tscn"
}
var current_level: Level = Level.SUBWAY
