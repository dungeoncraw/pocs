# Platform (Bevy)

A small platform shooter prototype inspired by Contra (NES). It uses the Bevy game engine to render a character with a texture-atlas-based animation, simple fixed-timestep physics (gravity + ground), and auto-firing bullets.

## Requirements
- Rust toolchain with Cargo (install via https://rustup.rs)
- Bevy 0.16.1 (declared in Cargo.toml)
- Desktop OS supported by Bevy (Windows, macOS, Linux)

## How to run
1. From the project directory (containing Cargo.toml), run:

   cargo run

2. Bevy will compile and launch a window. Assets are loaded from the assets/ folder (e.g., assets/character/character.png).

## Controls
- A / D: Move left / right
- Space: Jump (basic ground check included)
- Auto-fire: The player automatically fires a bullet about once per second facing the current direction

Notes:
- The animation plays only while moving. When idle, the sprite shows the first frame.
- Auto-firing is timer-based (no input needed) and bullets despawn by lifetime or world bounds.

## Project structure
- Cargo.toml: Dependencies and project manifest.
- src/main.rs: App setup and systems. Handles input, physics integration with a fixed timestep, rendering interpolation, bullet spawn/move/despawn, and animation.
- src/modules/
  - animation.rs: AnimationConfig and systems to advance texture atlas frames for entities that have the Animate marker.
  - sprite.rs: Helpers to load images and build TextureAtlasLayout from a grid; simple marker components and SpriteMap enum.
- assets/
  - character/character.png, worm.png: Sprite sheets used by the demo.

## Implementation details
- Rendering: Camera2d and Sprite with a TextureAtlas created via TextureAtlasLayout::from_grid.
- Animation: Frame timer is recreated per frame step; runs only while Animate marker is present.
- Input: A/D for horizontal movement; Space to jump when grounded; sprite.flip_x reflects facing.
- Physics/time: Velocity integrated on FixedUpdate (with gravity), a simple ground constraint at y=0, and render-time interpolation between previous/current physics states for smoothness.
- Shooting: Auto-fire system spawns a small triangle bullet in front of the player, moving along X based on facing; bullets despawn after ~3s or when too far away.

## Troubleshooting
- First build is slow due to dependencies; subsequent runs are faster.
- If assets are missing, ensure the assets/ folder exists and paths match those in src/modules/sprite.rs (e.g., character/character.png).
- On macOS, you may need to allow windowing permissions.

## License
Specify your preferred license here (e.g., MIT/Apache-2.0).