mod modules;

use crate::modules::sprite::{SpriteMap, generate_sprite_atlas};
use bevy::{color::palettes::tailwind, input::mouse::AccumulatedMouseMotion, prelude::*};
use bevy::{input::common_conditions::input_just_pressed, prelude::*};
use bevy::render::mesh::{Indices, Mesh, Mesh2d};
use bevy::render::render_resource::PrimitiveTopology;
use bevy_sprite::{ColorMaterial, MeshMaterial2d};
use modules::animation;
use modules::sprite::{LeftSprite, RightSprite};

// Entry point: sets up Bevy app, resources, and systems.
// Uses a fixed main loop for physics and interpolation for smooth rendering.
fn main() {
    App::new()
        // TODO make full screen
        .add_plugins(DefaultPlugins.set(ImagePlugin::default_nearest())) // prevents blurry sprites
        .init_resource::<DidFixedTimestepRunThisFrame>()
        .init_resource::<FireTimer>()
        .add_systems(Startup, setup)
        .add_systems(PreUpdate, clear_fixed_timestep_flag)
        .add_systems(FixedPreUpdate, set_fixed_time_step_flag)
        .add_systems(FixedUpdate, advance_physics)
        .add_systems(Update, (
            animation::execute_animations,
            auto_fire_system,
            bullet_movement_system,
            bullet_despawn_system,
        ))
        .add_systems(
            RunFixedMainLoop,
            (
                (accumulate_input)
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

#[derive(Debug, Component, Clone, Copy, PartialEq, Default, Deref, DerefMut)]
struct Grounded(bool);

#[derive(Resource)]
struct FireTimer(Timer);

impl Default for FireTimer {
    fn default() -> Self {
        // Auto-fire every 1 second
        Self(Timer::from_seconds(1.0, TimerMode::Repeating))
    }
}

#[derive(Component)]
struct Bullet {
    dir: f32,
    lifetime: Timer,
}

fn setup(
    mut commands: Commands,
    asset_server: Res<AssetServer>,
    mut texture_atlas_layouts: ResMut<Assets<TextureAtlasLayout>>,
) {
    commands.spawn((Camera2d::default(), CameraSensitivity::default()));

    let (character_texture, character_texture_atlas_layout, animation_config_2) =
        generate_sprite_atlas(
            &asset_server,
            &mut texture_atlas_layouts,
            SpriteMap::Character,
        );

    commands.spawn((
        Name::new("Player"),
        Sprite {
            image: character_texture.clone(),
            texture_atlas: Some(TextureAtlas {
                layout: character_texture_atlas_layout.clone(),
                index: animation_config_2.first_sprite_index,
            }),
            ..Default::default()
        },
        // this defines the size of the sprite
        Transform::from_scale(Vec3::splat(1.3)),
        RightSprite,
        animation_config_2,
        AccumulatedInput::default(),
        Velocity::default(),
        PhysicalTranslation::default(),
        PreviousPhysicalTranslation::default(),
        Grounded(true),
    ));
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

// Gathers player input for left/right movement and jump, updates velocity,
// flips sprite orientation, and toggles animation marker based on movement.
fn accumulate_input(
    keyboard_input: Res<ButtonInput<KeyCode>>,
    mut player: Single<(
        &mut AccumulatedInput,
        &mut Velocity,
        &mut Sprite,
        &animation::AnimationConfig,
        &mut Grounded,
        Entity,
    )>,
    mut commands: Commands,
) {
    const SPEED: f32 = 100.0;
    const JUMP_SPEED: f32 = 300.0;
    let (mut input, mut velocity, mut sprite, anim_config, mut grounded, entity) = player.into_inner();

    // Reset and collect only horizontal input for 2D
    input.movement = Vec2::ZERO;
    let left = keyboard_input.pressed(KeyCode::KeyA);
    let right = keyboard_input.pressed(KeyCode::KeyD);
    let jump = keyboard_input.just_pressed(KeyCode::Space);
    // identify if it is moving be left or right using bitwise XOR
    let moving = (left as u8 ^ right as u8) == 1;
    let dir = if right { 1.0 } else if left { -1.0 } else { 0.0 };

    // Set horizontal velocity in 2D (X axis), preserve vertical for jump/gravity
    velocity.0.x = dir * SPEED;

    // Handle jump on spacebar if grounded
    if jump && grounded.0 {
        velocity.0.y = JUMP_SPEED;
        grounded.0 = false;
    }

    // Flip sprite based on a direction: left => flip_x, right => not flipped
    if moving {
        sprite.flip_x = left;
    }

    // Toggle animation marker based on movement
    if moving {
        if let Ok(mut ew) = commands.get_entity(entity) {
            ew.insert(animation::Animate);
        }
    } else {
        if let Ok(mut ew) = commands.get_entity(entity) {
            ew.remove::<animation::Animate>();
        }
        // Reset to idle frame when not moving
        if let Some(atlas) = &mut sprite.texture_atlas {
            atlas.index = anim_config.first_sprite_index;
        }
    }
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

// Fixed-timestep physics: integrates velocity with gravity, applies ground constraint,
// and tracks previous/current physical translations for later interpolation.
fn advance_physics(
    fixed_time: Res<Time<Fixed>>,
    mut query: Query<(
        &mut PhysicalTranslation,
        &mut PreviousPhysicalTranslation,
        &mut Velocity,
        &mut Grounded,
    )>,
) {
    const GRAVITY: f32 = -900.0;
    let dt = fixed_time.delta_secs();
    for (mut current_physical_translation, mut previous_physical_translation, mut velocity, mut grounded) in
        query.iter_mut()
    {
        // Apply gravity
        velocity.0.y += GRAVITY * dt;

        // Save previous and integrate
        previous_physical_translation.0 = current_physical_translation.0;
        current_physical_translation.0 += velocity.0 * dt;

        // Simple ground at y = 0
        if current_physical_translation.0.y <= 0.0 {
            current_physical_translation.0.y = 0.0;
            if velocity.0.y < 0.0 {
                velocity.0.y = 0.0;
            }
            grounded.0 = true;
        } else {
            grounded.0 = false;
        }
    }
}

// Interpolates the visible transform between previous/current physics states
// to achieve smooth rendering even when physics runs at a fixed timestep.
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

fn make_triangle_mesh(size: f32) -> Mesh {
    // Create a simple 2D triangle in the XY plane
    let half = size * 0.5;
    let points = vec![
        // Up point
        [0.0, half, 0.0],
        // Bottom-left
        [-half, -half, 0.0],
        // Bottom-right
        [half, -half, 0.0],
    ];

    let mut mesh = Mesh::new(PrimitiveTopology::TriangleList, default());
    mesh.insert_attribute(Mesh::ATTRIBUTE_POSITION, points);
    // Single triangle indices
    mesh.insert_indices(Indices::U32(vec![0, 1, 2]));
    mesh
}

// Spawns a simple triangular bullet every time the repeating timer finishes.
// Bullet direction follows the player's facing (flip_x). Lifetime and world-bounds cleanup are handled elsewhere.
fn auto_fire_system(
    time: Res<Time>,
    mut timer: ResMut<FireTimer>,
    player_q: Single<(&Transform, &Sprite)>,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<ColorMaterial>>,
    mut commands: Commands,
) {
    timer.0.tick(time.delta());
    if !timer.0.just_finished() {
        return;
    }

    let (player_transform, player_sprite) = player_q.into_inner();

    // If flip_x = true, means the player is facing left, so we need to move the bullet to the right
    let dir = if player_sprite.flip_x { -1.0 } else { 1.0 };

    let mesh_handle = meshes.add(make_triangle_mesh(10.0));
    let material_handle = materials.add(ColorMaterial::from_color(tailwind::ORANGE_400));

    // translation gets the position of the player
    let spawn_pos = player_transform.translation + Vec3::new(12.0 * dir, 12.0, 0.0);

    commands.spawn((
        Name::new("Bullet"),
        Mesh2d(mesh_handle),
        MeshMaterial2d(material_handle),
        Transform::from_translation(spawn_pos).with_scale(Vec3::splat(1.0)),
        Bullet {
            dir,
            lifetime: Timer::from_seconds(3.0, TimerMode::Once),
        },
    ));
}

// Moves bullets horizontally based on their direction at a constant speed.
fn bullet_movement_system(time: Res<Time>, mut q: Query<(&mut Transform, &Bullet)>) {
    const SPEED: f32 = 400.0;
    let dt = time.delta_secs();
    for (mut transform, bullet) in &mut q {
        transform.translation.x += bullet.dir * SPEED * dt;
    }
}

// Despawns bullets once their lifetime ends or if they travel too far (world-bounds heuristic).
fn bullet_despawn_system(
    time: Res<Time>,
    mut commands: Commands,
    mut q: Query<(Entity, &mut Bullet, &Transform)>,
) {
    // World bounds heuristic for despawn (in case lifetime misses)
    const MAX_X: f32 = 2000.0;
    for (entity, mut bullet, transform) in &mut q {
        bullet.lifetime.tick(time.delta());
        if bullet.lifetime.finished() || transform.translation.x.abs() > MAX_X {
            let _ = commands.get_entity(entity).map(|mut ew| ew.despawn());
        }
    }
}
