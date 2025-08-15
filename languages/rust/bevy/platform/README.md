# Platform (Bevy)

A small platform shooting style game prototype inspired by Contra (NES). This is a platform shooting style game like Contra from the NES. It uses the Bevy game engine to render a character sprite with a texture atlas and simple movement/animation systems.

## Requirements
- Rust toolchain with Cargo (install via https://rustup.rs)
- This project targets Bevy 0.16.1 (declared in Cargo.toml)
- A desktop OS supported by Bevy (Windows, macOS, Linux)

## How to run
1. Ensure you are in the project directory (the one containing Cargo.toml).
2. Run the game:
   
   cargo run

Bevy will compile the project and open a window. Assets are loaded from the assets/ folder (e.g., assets/character/character.png).

## Controls
- A / D: Move left / right
- The character flips when walking left and returns to idle when you stop moving.

Note: Vertical movement and shooting are not implemented yet; this is an early platform shooter foundation focusing on horizontal movement and animation.

## Project structure
- Cargo.toml: Project manifest with dependencies (bevy, bevy_sprite).
- src/main.rs: Game setup. Spawns a 2D camera and the player sprite, processes input (A/D), updates velocity, flips the sprite based on direction, and toggles animation (idle vs. walking).
- src/modules/
  - animation.rs: AnimationConfig component and systems to advance texture atlas frames while the entity has the Animate marker.
  - sprite.rs: Helpers to load images and create TextureAtlasLayout from a sprite sheet; also defines simple marker components (LeftSprite, RightSprite) and the SpriteMap enum.
- assets/
  - character/character.png, worm.png: Sprite sheets used by the demo.

## Implementation details
- Rendering: Camera2d and Bevy’s Sprite with a TextureAtlas. The character uses a sprite sheet cut into frames by TextureAtlasLayout::from_grid.
- Animation: Frames advance only while moving (Animate marker present); otherwise the sprite shows the first/idle frame.
- Input: Reads keyboard A/D for left/right. The code computes velocity and flips the sprite horizontally when moving left.
- Time/Physics: There are components for accumulating input and interpolating a rendered transform over a fixed timestep to keep motion smooth.

## Troubleshooting
- First build is slow: Bevy compiles many dependencies; subsequent runs are faster.
- Missing assets: Ensure the assets folder is present and paths match those in src/modules/sprite.rs (e.g., character/character.png).
- macOS security prompts: You may need to grant screen/window permissions depending on OS settings.

## License
Specify your preferred license here (e.g., MIT/Apache-2.0).