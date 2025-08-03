use bevy::asset::{AssetServer, Assets, Handle};
use bevy::image::{Image, TextureAtlasLayout};
use bevy::math::UVec2;
use bevy::prelude::{Component, Res, ResMut};

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

fn map_from_sprite_map(sprite_map: SpriteMap) -> (String, SpriteGrid) {
    match sprite_map {
        SpriteMap::Character => ("character/worm-run-idle.png".to_string(), SpriteGrid {
            tile_size: UVec2::new(16, 20),
            columns: 31,
            rows: 1,
            padding: None,
            offset: None,
        }),
        SpriteMap::Worm => ("character/worm-run-idle.png".to_string(), SpriteGrid {
            tile_size: UVec2::new(16, 20),
            columns: 31,
            rows: 1,
            padding: None,
            offset: None,
        })
    }
}
// "character/worm-run-idle.png"
pub fn generate_sprite_atlas(asset_server: &Res<AssetServer>, texture_atlas_layouts: &mut ResMut<Assets<TextureAtlasLayout>>, asset_name: SpriteMap) -> (Handle<Image>, Handle<TextureAtlasLayout>) {
    let (asset_name, grid) = map_from_sprite_map(asset_name);
    // Ok this is a not ready sample, but is what can be found
    let texture: Handle<Image> = asset_server.load(asset_name);

    // Ok finally got the idea here, so the character sheet has a 16x20px animation with 31 frames
    // And the TextureAtlasLayout::from_grid return a zero-indexed layout, so the first animation must start with zero index value
    let layout = TextureAtlasLayout::from_grid(grid.tile_size, grid.columns, grid.rows, grid.padding, grid.offset);
    let texture_atlas_layout = texture_atlas_layouts.add(layout);
    (texture, texture_atlas_layout)
}