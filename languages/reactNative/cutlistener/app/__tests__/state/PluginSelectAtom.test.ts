import { pluginSelectAtom } from '@/app/state/PluginSelectAtom';
import { PluginType } from '@/app/types/types';

describe('pluginSelectAtom', () => {
  it('should have the correct default value', () => {
    // Get the default value of the atom
    const defaultValue = pluginSelectAtom.init;
    
    // Check that the default value is REACT_NATIVE_AUDIO_RECORDER_PLAYER
    expect(defaultValue).toBe(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER);
  });
});