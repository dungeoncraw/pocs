class_name AudioManager
extends Node

var players : Array[AudioStreamPlayer]

func _enter_tree():
	ManagerRegistry.register("audio_manager", self)

func _exit_tree():
	ManagerRegistry.unregister("audio_manager")

# Plays a sound effect
func play (stream : AudioStream):
	var player : AudioStreamPlayer = _get_player()
	player.stream = stream
	player.play()

# If we have a player that isn't currently being used - return that
# Otherwise, create a new one and return it
func _get_player () -> AudioStreamPlayer:
	for player in players:
		if not player.playing:
			return player
	
	var player : AudioStreamPlayer = AudioStreamPlayer.new()
	add_child(player)
	players.append(player)
	return player
