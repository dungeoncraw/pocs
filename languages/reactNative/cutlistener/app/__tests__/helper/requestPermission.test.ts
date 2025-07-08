import { Platform, Alert } from 'react-native';
import { requestMicrophonePermission } from '@/app/helper/requestPermission';
import { checkMultiple, requestMultiple, openSettings, PERMISSIONS } from 'react-native-permissions';

// Mock the dependencies
jest.mock('react-native', () => ({
  Platform: {
    OS: 'ios',
    select: jest.fn()
  },
  Alert: {
    alert: jest.fn()
  }
}));

jest.mock('react-native-permissions', () => ({
  checkMultiple: jest.fn(),
  requestMultiple: jest.fn(),
  openSettings: jest.fn(),
  PERMISSIONS: {
    IOS: {
      MICROPHONE: 'ios.microphone'
    },
    ANDROID: {
      RECORD_AUDIO: 'android.record_audio',
      WRITE_EXTERNAL_STORAGE: 'android.write_external_storage',
      READ_EXTERNAL_STORAGE: 'android.read_external_storage',
      READ_MEDIA_AUDIO: 'android.read_media_audio'
    }
  }
}));

describe('requestMicrophonePermission', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should request microphone permission on iOS and return true when granted', async () => {
    // Set platform to iOS
    Platform.OS = 'ios';
    
    // Mock the requestMultiple function to return granted permission
    (requestMultiple as jest.Mock).mockResolvedValue({
      'ios.microphone': 'granted'
    });

    const result = await requestMicrophonePermission();
    
    // Verify requestMultiple was called with the correct permissions
    expect(requestMultiple).toHaveBeenCalledWith([PERMISSIONS.IOS.MICROPHONE]);
    
    // Verify the function returns true when permission is granted
    expect(result).toBe(true);
  });

  it('should request microphone permission on iOS and return false when denied', async () => {
    // Set platform to iOS
    Platform.OS = 'ios';
    
    // Mock the requestMultiple function to return denied permission
    (requestMultiple as jest.Mock).mockResolvedValue({
      'ios.microphone': 'denied'
    });

    const result = await requestMicrophonePermission();
    
    // Verify requestMultiple was called with the correct permissions
    expect(requestMultiple).toHaveBeenCalledWith([PERMISSIONS.IOS.MICROPHONE]);
    
    // Verify the function returns false when permission is denied
    expect(result).toBe(false);
  });

  it('should check and request permissions on Android and return true when all granted', async () => {
    // Set platform to Android
    Platform.OS = 'android';
    
    // Mock the checkMultiple function to return all permissions as granted
    (checkMultiple as jest.Mock).mockResolvedValue({
      'android.record_audio': 'granted',
      'android.write_external_storage': 'granted',
      'android.read_external_storage': 'granted',
      'android.read_media_audio': 'granted'
    });
    
    // Mock the requestMultiple function to return all permissions as granted
    (requestMultiple as jest.Mock).mockResolvedValue({
      'android.record_audio': 'granted',
      'android.write_external_storage': 'granted',
      'android.read_external_storage': 'granted'
    });

    const result = await requestMicrophonePermission();
    
    // Verify checkMultiple was called with the correct permissions
    expect(checkMultiple).toHaveBeenCalledWith([
      PERMISSIONS.ANDROID.RECORD_AUDIO,
      PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE,
      PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
      PERMISSIONS.ANDROID.READ_MEDIA_AUDIO
    ]);
    
    // Verify requestMultiple was called with the correct permissions
    expect(requestMultiple).toHaveBeenCalledWith([
      PERMISSIONS.ANDROID.RECORD_AUDIO,
      PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE,
      PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE
    ]);
    
    // Verify the function returns true when all permissions are granted
    expect(result).toBe(true);
  });

  it('should show an alert and return false when permissions are blocked on Android', async () => {
    // Set platform to Android
    Platform.OS = 'android';
    
    // Mock the checkMultiple function to return some permissions as blocked
    (checkMultiple as jest.Mock).mockResolvedValue({
      'android.record_audio': 'blocked',
      'android.write_external_storage': 'granted',
      'android.read_external_storage': 'granted',
      'android.read_media_audio': 'granted'
    });

    const result = await requestMicrophonePermission();
    
    // Verify Alert.alert was called
    expect(Alert.alert).toHaveBeenCalled();
    
    // Verify the function returns false when permissions are blocked
    expect(result).toBe(false);
  });

  it('should return false for unsupported platforms', async () => {
    // Set platform to an unsupported value
    Platform.OS = 'web';

    const result = await requestMicrophonePermission();
    
    // Verify the function returns false for unsupported platforms
    expect(result).toBe(false);
  });
});