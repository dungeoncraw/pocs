import {Alert, Platform} from 'react-native';
import {checkMultiple, PERMISSIONS, requestMultiple, openSettings } from 'react-native-permissions';

export const requestMicrophonePermission = async () => {
    if (Platform.OS === 'ios') {
        const result = await requestMultiple([PERMISSIONS.IOS.MICROPHONE]);
        return result[PERMISSIONS.IOS.MICROPHONE] === 'granted';
    } else if (Platform.OS === 'android') {
        const permissionStatus = await checkMultiple([
            PERMISSIONS.ANDROID.RECORD_AUDIO,
            PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE,
            PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
            PERMISSIONS.ANDROID.READ_MEDIA_AUDIO,
        ]);
        const hasNeverAskAgain = Object.values(permissionStatus).some(
            status => status === 'blocked' || status === 'denied'
        );
        if (hasNeverAskAgain) {
            Alert.alert(
                'Permission required',
                'To record audio, app need access to microphone and storage. Please grant the access on settings.',
                [
                    {
                        text: 'Cancel',
                        style: 'cancel'
                    },
                    {
                        text: 'Open Settings',
                        onPress: () => openSettings()
                    }
                ]
            );
            return false;
        }

        const result = await requestMultiple([
            PERMISSIONS.ANDROID.RECORD_AUDIO,
            PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE,
            PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
        ]);
        return Object.values(result).every(status => status === 'granted');
    }
    return false;
};