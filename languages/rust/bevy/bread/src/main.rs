mod hello_plugin;

use bevy::prelude::*;
use crate::hello_plugin::HelloPlugin;

fn main() {
    App::new()
        // common plugins like UI, 2D, 3D, asset loading
        .add_plugins(DefaultPlugins)
        .add_plugins(HelloPlugin)
        .run();
}