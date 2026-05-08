extends Node

@export var enemy_pool: Node
@export var spawn_points: Array[Node2D]
@export var start_enemies_per_second: float = 0.5
@export var enemies_per_second_increase_rate: float = 0.01

@onready var enemies_per_second: float = start_enemies_per_second
var spawn_rate: float

@onready var spawn_timer: Timer = $SpawnTimer

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	_on_spawn_timer_timeout()


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta: float) -> void:
	enemies_per_second += enemies_per_second_increase_rate * delta
	spawn_rate = 1.0 / enemies_per_second


func _on_spawn_timer_timeout() -> void:
	var enemy = enemy_pool.spawn()
	var spawn_point = spawn_points[randi_range(0, len(spawn_points) -1)].global_position
	enemy.global_position = spawn_point
	spawn_timer.start(spawn_rate)
