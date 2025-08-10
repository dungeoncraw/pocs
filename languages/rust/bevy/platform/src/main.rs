mod modules;

use crate::modules::sprite::{SpriteMap, generate_sprite_atlas};
use bevy::{color::palettes::tailwind, input::mouse::AccumulatedMouseMotion, prelude::*};
use bevy::{input::common_conditions::input_just_pressed, prelude::*};
use modules::animation;
use modules::sprite::{LeftSprite, RightSprite};
use std::f32::consts::FRAC_PI_2;

fn main() {
    App::new()
        // TODO make full screen
        .add_plugins(DefaultPlugins.set(ImagePlugin::default_nearest())) // prevents blurry sprites
        .init_resource::<DidFixedTimestepRunThisFrame>()
        .add_systems(Startup, setup)
        .add_systems(PreUpdate, clear_fixed_timestep_flag)
        .add_systems(FixedPreUpdate, set_fixed_time_step_flag)
        .add_systems(FixedUpdate, advance_physics)
        .add_systems(Update, animation::execute_animations)
        .add_systems(
            RunFixedMainLoop,
            (
                (rotate_camera, accumulate_input)
                    .chain()
                    .in_set(RunFixedMainLoopSystem::BeforeFixedMainLoop),
                (
                    clear_input.run_if(did_fixed_timestep_run_this_frame),
                    interpolate_rendered_transform,
                )
                    .chain()
                    .in_set(RunFixedMainLoopSystem::AfterFixedMainLoop),
            ),
        )
        // .add_systems(
        //     Update,
        //     (
        //         // Press the right arrow key to animate the right sprite
        //         animation::trigger_animation::<RightSprite>
        //             .run_if(input_just_pressed(KeyCode::ArrowRight)),
        //         // Press the left arrow key to animate the left sprite
        //         animation::trigger_animation::<LeftSprite>
        //             .run_if(input_just_pressed(KeyCode::ArrowLeft)),
        //     ),
        // )
        .run();
}


#[derive(Debug, Component, Clone, Copy, PartialEq, Default, Deref, DerefMut)]
struct  AccumulatedInput {
    movement: Vec2,
}
#[derive(Debug, Component, Clone, Copy, PartialEq, Default, Deref, DerefMut)]
struct Velocity(Vec3);

#[derive(Debug, Component, Clone, Copy, PartialEq, Default, Deref, DerefMut)]
struct PhysicalTranslation(Vec3);

#[derive(Debug, Component, Clone, Copy, PartialEq, Default, Deref, DerefMut)]
struct PreviousPhysicalTranslation(Vec3);

fn setup(
    mut commands: Commands,
    asset_server: Res<AssetServer>,
    mut texture_atlas_layouts: ResMut<Assets<TextureAtlasLayout>>,
) {
    commands.spawn((Camera3d::default(), CameraSensitivity::default()));

    // let (worm_texture, worm_texture_atlas_layout, animation_config_1) =
    //     generate_sprite_atlas(&asset_server, &mut texture_atlas_layouts, SpriteMap::Worm);
    //
    // // Create the first (left-hand) sprite
    // commands.spawn((
    //     Sprite {
    //         image: worm_texture.clone(),
    //         texture_atlas: Some(TextureAtlas {
    //             layout: worm_texture_atlas_layout.clone(),
    //             index: animation_config_1.first_sprite_index,
    //         }),
    //         ..default()
    //     },
    //     Transform::from_scale(Vec3::splat(6.0)).with_translation(Vec3::new(-70.0, 250.0, 0.0)),
    //     LeftSprite,
    //     animation_config_1,
    // ));

    let (character_texture, character_texture_atlas_layout, animation_config_2) =
        generate_sprite_atlas(
            &asset_server,
            &mut texture_atlas_layouts,
            SpriteMap::Character,
        );

    // Create the second (right-hand) sprite
    commands.spawn((
        Name::new("Player"),
        Sprite {
            image: character_texture.clone(),
            texture_atlas: Some(TextureAtlas {
                layout: character_texture_atlas_layout.clone(),
                index: animation_config_2.first_sprite_index,
            }),
            // TODO this flips the character, so need to make it based on the keyboard input
            // flip_x: true,
            ..Default::default()
        },
        Transform::from_scale(Vec3::splat(0.3)),
        // RightSprite,
        animation_config_2,
        AccumulatedInput::default(),
        Velocity::default(),
        PhysicalTranslation::default(),
        PreviousPhysicalTranslation::default(),
    ));
    // commands.spawn((
    //     Name::new("Player"),
    //     Sprite::from_image(asset_server.load("icon.png")),
    //     Transform::from_scale(Vec3::splat(0.3)),
    //     AccumulatedInput::default(),
    //     Velocity::default(),
    //     PhysicalTranslation::default(),
    //     PreviousPhysicalTranslation::default(),
    // ));
}

fn rotate_camera(
    accumulated_mouse_motion: Res<AccumulatedMouseMotion>,
    player: Single<(&mut Transform, &CameraSensitivity), With<Camera>>,
) {
    let (mut transform, camera_sensitivity) = player.into_inner();

    let delta = accumulated_mouse_motion.delta;

    if delta != Vec2::ZERO {
        // Note that we are not multiplying by delta time here.
        // The reason is that for mouse movement, we already get the full movement that happened since the last frame.
        // This means that if we multiply by delta time, we will get a smaller rotation than intended by the user.
        let delta_yaw = -delta.x * camera_sensitivity.x;
        let delta_pitch = -delta.y * camera_sensitivity.y;

        let (yaw, pitch, roll) = transform.rotation.to_euler(EulerRot::YXZ);
        let yaw = yaw + delta_yaw;

        // If the pitch was ±¹⁄₂ π, the camera would look straight up or down.
        // When the user wants to move the camera back to the horizon, which way should the camera face?
        // The camera has no way of knowing what direction was "forward" before landing in that extreme position,
        // so the direction picked will for all intents and purposes be arbitrary.
        // Another issue is that for mathematical reasons, the yaw will effectively be flipped when the pitch is at the extremes.
        // To not run into these issues, we clamp the pitch to a safe range.
        const PITCH_LIMIT: f32 = FRAC_PI_2 - 0.01;
        let pitch = (pitch + delta_pitch).clamp(-PITCH_LIMIT, PITCH_LIMIT);

        transform.rotation = Quat::from_euler(EulerRot::YXZ, yaw, pitch, roll);
    }
}
#[derive(Debug, Component, Deref, DerefMut)]
struct CameraSensitivity(Vec2);

impl Default for CameraSensitivity {
    fn default() -> Self {
        Self(
            Vec2::new(0.003, 0.002),
        )
    }
}

fn accumulate_input(
    keyboard_input: Res<ButtonInput<KeyCode>>,
    player: Single<(&mut AccumulatedInput, &mut Velocity)>,
    camera: Single<&Transform, With<Camera>>,
) {
    const SPEED: f32 = 4.0;
    let (mut input, mut velocity) = player.into_inner();
    input.movement = Vec2::ZERO;
    if keyboard_input.pressed(KeyCode::KeyW) {
        input.movement.y += 1.0;
    }
    if keyboard_input.pressed(KeyCode::KeyS) {
        input.movement.y -= 1.0;
    }
    if keyboard_input.pressed(KeyCode::KeyA) {
        input.movement.x -= 1.0;
    }
    if keyboard_input.pressed(KeyCode::KeyD) {
        input.movement.x += 1.0;
    }

    let input_3d = Vec3 {
        x: input.movement.x,
        y: 0.0,
        z: -input.movement.y,
    };

    let rotated_input = camera.rotation * input_3d;

    velocity.0 = rotated_input.clamp_length_max(1.0) * SPEED;
}

#[derive(Resource, Debug, Deref, DerefMut, Default)]
pub struct DidFixedTimestepRunThisFrame(bool);

/// Reset the flag at the start of every frame.
fn clear_fixed_timestep_flag(
    mut did_fixed_timestep_run_this_frame: ResMut<DidFixedTimestepRunThisFrame>,
) {
    did_fixed_timestep_run_this_frame.0 = false;
}

/// Set the flag during each fixed timestep.
fn set_fixed_time_step_flag(
    mut did_fixed_timestep_run_this_frame: ResMut<DidFixedTimestepRunThisFrame>,
) {
    did_fixed_timestep_run_this_frame.0 = true;
}

fn did_fixed_timestep_run_this_frame(
    did_fixed_timestep_run_this_frame: Res<DidFixedTimestepRunThisFrame>,
) -> bool {
    did_fixed_timestep_run_this_frame.0
}

// Clear the input after it was processed in the fixed timestep.
fn clear_input(mut input: Single<&mut AccumulatedInput>) {
    **input = AccumulatedInput::default();
}

fn advance_physics(
    fixed_time: Res<Time<Fixed>>,
    mut query: Query<(
        &mut PhysicalTranslation,
        &mut PreviousPhysicalTranslation,
        &Velocity,
    )>,
) {
    for (mut current_physical_translation, mut previous_physical_translation, velocity) in
        query.iter_mut()
    {
        previous_physical_translation.0 = current_physical_translation.0;
        current_physical_translation.0 += velocity.0 * fixed_time.delta_secs();
    }
}

fn interpolate_rendered_transform(
    fixed_time: Res<Time<Fixed>>,
    mut query: Query<(
        &mut Transform,
        &PhysicalTranslation,
        &PreviousPhysicalTranslation,
    )>,
) {
    for (mut transform, current_physical_translation, previous_physical_translation) in
        query.iter_mut()
    {
        let previous = previous_physical_translation.0;
        let current = current_physical_translation.0;
        // The overstep fraction is a value between 0 and 1 that tells us how far we are between two fixed timesteps.
        let alpha = fixed_time.overstep_fraction();

        let rendered_translation = previous.lerp(current, alpha);
        transform.translation = rendered_translation;
    }
}

// Sync the camera's position with the player's interpolated position
fn translate_camera(
    mut camera: Single<&mut Transform, With<Camera>>,
    player: Single<&Transform, (With<AccumulatedInput>, Without<Camera>)>,
) {
    camera.translation = player.translation;
}