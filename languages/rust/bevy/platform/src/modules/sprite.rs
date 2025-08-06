use bevy::asset::{AssetServer, Assets, Handle};
use bevy::image::{Image, TextureAtlasLayout};
use bevy::math::UVec2;
use bevy::prelude::{Component, Res, ResMut};
use crate::modules::animation::AnimationConfig;

#[derive(Component)]
pub struct LeftSprite;

#[derive(Component)]
pub struct RightSprite;

struct SpriteGrid {
    tile_size: UVec2,
    columns: u32,
    rows: u32,
    padding: Option<UVec2>,
    offset: Option<UVec2>,
}

pub enum SpriteMap {
    Character,
    Worm
}
/// * `tile_size` - Just a simple size of the character, this is not so easy to get
/// * `columns` - Numbers of columns for animation
/// * `rows` - Number of rows for animation
/// * `padding` - Optional padding is the space between sprites, so for a single row, no Y padding just X padding if needed
/// * `offset` - Optional offset from pixel 0x0 of a file to the place where sprite starts
fn map_from_sprite_map(sprite_map: SpriteMap) -> (String, SpriteGrid, AnimationConfig) {
    match sprite_map {
        SpriteMap::Character => ("character/character.png".to_string(), SpriteGrid {
            tile_size: UVec2::new(32, 38),
            columns: 6,
            rows: 1,
            padding: Some(UVec2::new(3, 0)),
            offset: Some(UVec2::new(6, 3)),
        }, AnimationConfig::new(0, 5, 2)),
        SpriteMap::Worm => ("character/worm.png".to_string(), SpriteGrid {
            tile_size: UVec2::new(16, 20),
            columns: 31,
            rows: 1,
            padding: None,
            offset: None,
        }, AnimationConfig::new(0, 30, 10))
    }
}
// "character/worm.png"
pub fn generate_sprite_atlas(asset_server: &Res<AssetServer>, texture_atlas_layouts: &mut ResMut<Assets<TextureAtlasLayout>>, asset_name: SpriteMap) -> (Handle<Image>, Handle<TextureAtlasLayout>, AnimationConfig) {
    let (asset_name, grid, animation_config) = map_from_sprite_map(asset_name);
    let texture: Handle<Image> = asset_server.load(asset_name);

    // Ok finally got the idea here, so the character sheet has a 16x20px animation with 31 frames
    // And the TextureAtlasLayout::from_grid return a zero-indexed layout, so the first animation must start with zero index value
    let layout = TextureAtlasLayout::from_grid(grid.tile_size, grid.columns, grid.rows, grid.padding, grid.offset);
    let texture_atlas_layout = texture_atlas_layouts.add(layout);
    (texture, texture_atlas_layout, animation_config)
}