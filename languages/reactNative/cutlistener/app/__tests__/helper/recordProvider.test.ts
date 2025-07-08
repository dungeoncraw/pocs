import { Platform } from 'react-native';
import RecordProvider from '@/app/helper/recordProvider';
import { PluginType } from '@/app/types/types';
import { requestMicrophonePermission } from '@/app/helper/requestPermission';
import AudioRecorderPlayer from 'react-native-audio-recorder-player';

// Mock dependencies
jest.mock('react-native', () => ({
  Platform: {
    OS: 'ios',
    select: jest.fn((obj) => obj.ios)
  }
}));

jest.mock('react-native-audio-recorder-player', () => {
  return Object.assign(
    jest.fn().mockImplementation(() => ({
      startRecorder: jest.fn().mockResolvedValue('file://test/path'),
      stopRecorder: jest.fn().mockResolvedValue('file://test/path'),
      startPlayer: jest.fn().mockResolvedValue('player started'),
      setVolume: jest.fn().mockResolvedValue(1.0),
      addRecordBackListener: jest.fn(),
      removeRecordBackListener: jest.fn(),
      addPlayBackListener: jest.fn(),
      mmssss: jest.fn().mockReturnValue('00:01')
    })),
    {
      AudioEncoderAndroidType: {
        AAC: 'aac',
      },
      AudioSourceAndroidType: {
        MIC: 'mic',
      },
      AVEncoderAudioQualityIOSType: {
        high: 'high',
      },
      AVEncodingOption: {
        aac: 'aac',
      },
      OutputFormatAndroidType: {
        AAC_ADTS: 'aac_adts',
      }
    }
  );
});

jest.mock('expo-audio', () => ({
  createAudioPlayer: jest.fn().mockReturnValue({})
}));

jest.mock('@/app/helper/requestPermission', () => ({
  requestMicrophonePermission: jest.fn()
}));

describe('RecordProvider', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('constructor', () => {
    it('should initialize with REACT_NATIVE_AUDIO_RECORDER_PLAYER plugin', () => {
      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');

      expect(provider.pluginType).toBe(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER);
      expect(provider.path).toBe('test/path');
      expect(AudioRecorderPlayer).toHaveBeenCalled();
    });

    it('should initialize with EXPO_AUDIO plugin', () => {
      const provider = new RecordProvider(PluginType.EXPO_AUDIO, 'test/path');

      expect(provider.pluginType).toBe(PluginType.EXPO_AUDIO);
      expect(provider.path).toBe('test/path');
      expect(require('expo-audio').createAudioPlayer).toHaveBeenCalled();
    });
  });

  describe('onRecord', () => {
    it('should request permissions on Android and start recording when granted', async () => {
      // Set platform to Android
      Platform.OS = 'android';

      // Mock permission granted
      (requestMicrophonePermission as jest.Mock).mockResolvedValue(true);

      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');
      await provider.onRecord();

      expect(requestMicrophonePermission).toHaveBeenCalled();
      expect(provider.recordPLugin.startRecorder).toHaveBeenCalled();
      expect(provider.recordPLugin.addRecordBackListener).toHaveBeenCalled();
    });

    it('should not start recording on Android when permissions are denied', async () => {
      // Set platform to Android
      Platform.OS = 'android';

      // Mock permission denied
      (requestMicrophonePermission as jest.Mock).mockResolvedValue(false);

      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');
      await provider.onRecord();

      expect(requestMicrophonePermission).toHaveBeenCalled();
      expect(provider.recordPLugin.startRecorder).not.toHaveBeenCalled();
    });

    it('should start recording on iOS without checking permissions', async () => {
      // Set platform to iOS
      Platform.OS = 'ios';

      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');
      await provider.onRecord();

      expect(requestMicrophonePermission).not.toHaveBeenCalled();
      expect(provider.recordPLugin.startRecorder).toHaveBeenCalled();
      expect(provider.recordPLugin.addRecordBackListener).toHaveBeenCalled();
    });

    it('should handle record back listener callback', async () => {
      // Set platform to iOS
      Platform.OS = 'ios';

      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');

      // Mock the addRecordBackListener to capture and execute the callback
      provider.recordPLugin.addRecordBackListener = jest.fn().mockImplementation((callback) => {
        callback({ currentPosition: 1000 });
      });

      await provider.onRecord();

      expect(provider.recordSecs).toBe(1000);
      expect(provider.recordPLugin.mmssss).toHaveBeenCalledWith(1000);
    });

    it('should do nothing for EXPO_AUDIO plugin', async () => {
      const provider = new RecordProvider(PluginType.EXPO_AUDIO, 'test/path');
      await provider.onRecord();

      // No expectations needed as the method should do nothing for EXPO_AUDIO
    });
  });

  describe('onStop', () => {
    it('should stop recording for REACT_NATIVE_AUDIO_RECORDER_PLAYER plugin', async () => {
      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');
      await provider.onStop();

      expect(provider.recordPLugin.stopRecorder).toHaveBeenCalled();
      expect(provider.recordPLugin.removeRecordBackListener).toHaveBeenCalled();
      expect(provider.recordSecs).toBe(0);
    });

    it('should do nothing for EXPO_AUDIO plugin', async () => {
      const provider = new RecordProvider(PluginType.EXPO_AUDIO, 'test/path');
      await provider.onStop();

      // No expectations needed as the method should do nothing for EXPO_AUDIO
    });
  });

  describe('onPlay', () => {
    it('should start playing for REACT_NATIVE_AUDIO_RECORDER_PLAYER plugin', async () => {
      const provider = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, 'test/path');

      // Mock the addPlayBackListener to capture and execute the callback
      provider.recordPLugin.addPlayBackListener = jest.fn().mockImplementation((callback) => {
        callback({ currentPosition: 1000, duration: 5000 });
      });

      await provider.onPlay();

      expect(provider.recordPLugin.startPlayer).toHaveBeenCalledWith('test/path');
      expect(provider.recordPLugin.setVolume).toHaveBeenCalledWith(1.0);
      expect(provider.recordPLugin.addPlayBackListener).toHaveBeenCalled();

      // Check that the playback listener updated the state
      expect(provider.currentPositionSec).toBe(1000);
      expect(provider.currentDurationSec).toBe(5000);
      expect(provider.recordPLugin.mmssss).toHaveBeenCalledWith(1000);
      expect(provider.recordPLugin.mmssss).toHaveBeenCalledWith(5000);
    });

    it('should do nothing for EXPO_AUDIO plugin', async () => {
      const provider = new RecordProvider(PluginType.EXPO_AUDIO, 'test/path');
      await provider.onPlay();

      // No expectations needed as the method should do nothing for EXPO_AUDIO
    });
  });
});
