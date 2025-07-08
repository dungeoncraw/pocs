import { PluginType } from '@/app/types/types';

describe('PluginType', () => {
  it('should have the correct values', () => {
    expect(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER).toBe('RECORDER_PLAYER');
    expect(PluginType.EXPO_AUDIO).toBe('EXPO_AUDIO');
  });

  it('should have exactly two values', () => {
    const enumValues = Object.values(PluginType);
    expect(enumValues.length).toBe(2);
    expect(enumValues).toContain('RECORDER_PLAYER');
    expect(enumValues).toContain('EXPO_AUDIO');
  });
});