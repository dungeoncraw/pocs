const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

config.resolver.sourceExts.push('cjs', 'mjs');
config.resolver.assetExts.push('glb', 'gltf', 'obj', 'mtl', 'fbx');

module.exports = config;
