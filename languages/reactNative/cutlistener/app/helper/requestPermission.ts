import { Platform } from 'react-native';
import { PERMISSIONS, requestMultiple } from 'react-native-permissions';

export const requestMicrophonePermission = async () => {
    if (Platform.OS === 'ios') {
        const result = await requestMultiple([PERMISSIONS.IOS.MICROPHONE]);
        return result[PERMISSIONS.IOS.MICROPHONE] === 'granted';
    } else if (Platform.OS === 'android') {
        const result = await requestMultiple([PERMISSIONS.ANDROID.RECORD_AUDIO]);
        return result[PERMISSIONS.ANDROID.RECORD_AUDIO] === 'granted';
    }
    return false;
};