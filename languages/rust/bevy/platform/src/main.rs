// src/main.rs
mod player;
mod camera;

use bevy::prelude::*;
use player::*;
use camera::*;

fn main() {
    App::new()
        .add_plugins(DefaultPlugins.set(WindowPlugin {
            primary_window: Some(Window {
                title: "Contra Clone".into(),
                resolution: (800., 480.).into(),
                ..default()
            }),
            ..default()
        }))
        .add_startup_system(setup)
        .add_systems(Update, (player_movement, follow_player))
        .run();
}

fn setup(mut commands: Commands, asset_server: Res<AssetServer>) {
    // Câmera
    commands.spawn(Camera2dBundle::default());

    // Player inicial
    spawn_player(&mut commands, &asset_server);
}
