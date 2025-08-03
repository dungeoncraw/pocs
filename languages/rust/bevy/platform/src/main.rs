mod modules;

use bevy::{input::common_conditions::input_just_pressed, prelude::*};
use modules::animation;
use modules::animation::AnimationConfig;
use modules::sprite::{LeftSprite, RightSprite};
use crate::modules::sprite::{generate_sprite_atlas, SpriteMap};

fn main() {
    App::new()
        .add_plugins(DefaultPlugins.set(ImagePlugin::default_nearest())) // prevents blurry sprites
        .add_systems(Startup, setup)
        .add_systems(Update, animation::execute_animations)
        .add_systems(
            Update,
            (
                // Press the right arrow key to animate the right sprite
                animation::trigger_animation::<RightSprite>.run_if(input_just_pressed(KeyCode::ArrowRight)),
                // Press the left arrow key to animate the left sprite
                animation::trigger_animation::<LeftSprite>.run_if(input_just_pressed(KeyCode::ArrowLeft)),
            ),
        )
        .run();
}

fn setup(
    mut commands: Commands,
    asset_server: Res<AssetServer>,
    mut texture_atlas_layouts: ResMut<Assets<TextureAtlasLayout>>,
) {
    commands.spawn(Camera2d);

    // Create a minimal UI explaining how to interact with the example
    commands.spawn((
        Text::new("Left Arrow: Animate Left Sprite\nRight Arrow: Animate Right Sprite"),
        Node {
            position_type: PositionType::Absolute,
            top: Val::Px(12.0),
            left: Val::Px(12.0),
            ..default()
        },
    ));

    let (worm_texture, worm_texture_atlas_layout) = generate_sprite_atlas(&asset_server, &mut texture_atlas_layouts, SpriteMap::Worm);

    // The first (left-hand) sprite runs at 10 FPS
    let animation_config_1 = AnimationConfig::new(0, 30, 10);

    // Create the first (left-hand) sprite
    commands.spawn((
        Sprite {
            image: worm_texture.clone(),
            texture_atlas: Some(TextureAtlas {
                layout: worm_texture_atlas_layout.clone(),
                index: animation_config_1.first_sprite_index,
            }),
            ..default()
        },
        Transform::from_scale(Vec3::splat(6.0)).with_translation(Vec3::new(-70.0, 0.0, 0.0)),
        LeftSprite,
        animation_config_1,
    ));

    let (character_texture, character_texture_atlas_layout) = generate_sprite_atlas(&asset_server, &mut texture_atlas_layouts, SpriteMap::Character);
    // The second (right-hand) sprite runs at 30 FPS
    let animation_config_2 = AnimationConfig::new(0, 30, 30);

    // Create the second (right-hand) sprite
    commands.spawn((
        Sprite {
            image: character_texture.clone(),
            texture_atlas: Some(TextureAtlas {
                layout: character_texture_atlas_layout.clone(),
                index: animation_config_2.first_sprite_index,
            }),
            ..Default::default()
        },
        Transform::from_scale(Vec3::splat(6.0)).with_translation(Vec3::new(70.0, 0.0, 0.0)),
        RightSprite,
        animation_config_2,
    ));
}