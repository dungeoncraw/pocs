import AudioRecorderPlayer, {
    AudioEncoderAndroidType,
    AudioSet,
    AudioSourceAndroidType,
    AVEncoderAudioQualityIOSType,
    AVEncodingOption,
    OutputFormatAndroidType,
    PlayBackType,
    RecordBackType
} from "react-native-audio-recorder-player";
import {createAudioPlayer, AudioModule, requestRecordingPermissionsAsync, AudioStatus, RecorderState, AudioQuality, IOSOutputFormat, setAudioModeAsync, setIsAudioActiveAsync} from "expo-audio";
import {Platform} from "react-native";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import {PluginType} from "@/app/types/types";

class RecordProvider {
    pluginType: PluginType;
    recordPLugin: any;
    recordRecorder: any;
    path: string | undefined;
    recordSecs = 0;
    recordTime = '00:00';
    currentPositionSec= 0;
    currentDurationSec= 0;
    playTime=  '00:00';
    duration= '00:00';
    constructor(pluginType: PluginType, path?: string) {
        this.pluginType = pluginType;
        this.path = path || Platform.select({
            ios: undefined,
            android: undefined
        });
        switch (pluginType) {
            case PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER:
                this.recordPLugin = new AudioRecorderPlayer();
                break;
            case PluginType.EXPO_AUDIO:
                // Configure audio session for recording
                if (Platform.OS === 'ios') {
                    setAudioModeAsync({
                        playsInSilentMode: true,
                        interruptionMode: 'mixWithOthers',
                        allowsRecording: true,
                        shouldPlayInBackground: false
                    });
                } else {
                    setAudioModeAsync({
                        interruptionModeAndroid: 'duckOthers',
                        shouldPlayInBackground: false,
                        shouldRouteThroughEarpiece: false
                    });
                }

                // Activate audio session
                setIsAudioActiveAsync(true);

                this.recordPLugin = createAudioPlayer();
                this.recordRecorder = new AudioModule.AudioRecorder({});
        }
    }

    async onRecord() {
    try {
        if (this.pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
            if (Platform.OS === 'android') {
                try {
                    const hasPermissions = await requestMicrophonePermission();

                    if (!hasPermissions) {
                        console.log('Permission not granted');
                        return;
                    }
                } catch (err) {
                    console.warn(err);
                    return;
                }
            }

            const audioSet: AudioSet = {
                AudioEncoderAndroid: AudioEncoderAndroidType.AAC,
                AudioSourceAndroid: AudioSourceAndroidType.MIC,
                AVEncoderAudioQualityKeyIOS: AVEncoderAudioQualityIOSType.high,
                AVNumberOfChannelsKeyIOS: 2,
                AVFormatIDKeyIOS: AVEncodingOption.aac,
                OutputFormatAndroid: OutputFormatAndroidType.AAC_ADTS,
            };

            console.log('audioSet', audioSet);
            const uri = await this.recordPLugin.startRecorder(
                this.path,
                audioSet,
            );

            this.recordPLugin.addRecordBackListener((e: RecordBackType) => {
                this.recordSecs = e.currentPosition;
                this.recordTime = this.recordPLugin.mmssss(Math.floor(e.currentPosition));
            });

            console.log(`Gravação iniciada: ${uri}`);
        } else if (this.pluginType === PluginType.EXPO_AUDIO) {
            // Check permissions and configure audio session
            if (Platform.OS === 'android') {
                try {
                    const permissionResponse = await requestRecordingPermissionsAsync();

                    if (!permissionResponse.granted) {
                        console.log('Permission not granted');
                        return;
                    }
                } catch (err) {
                    console.warn(err);
                    return;
                }
            } else if (Platform.OS === 'ios') {
                try {
                    // Ensure audio session is configured for recording
                    await setAudioModeAsync({
                        playsInSilentMode: true,
                        interruptionMode: 'mixWithOthers',
                        allowsRecording: true,
                        shouldPlayInBackground: false
                    });

                    // Activate audio session
                    await setIsAudioActiveAsync(true);
                } catch (err) {
                    console.error('Error configuring audio session:', err);
                    return;
                }
            }

            await this.recordRecorder.prepareToRecordAsync({
                extension: '.m4a',
                sampleRate: 44100,
                numberOfChannels: 2,
                bitRate: 128000,
                android: {
                    extension: '.m4a',
                    outputFormat: 'aac_adts',
                    audioEncoder: 'aac',
                },
                ios: {
                    extension: '.m4a',
                    outputFormat: IOSOutputFormat.MPEG4AAC,
                    audioQuality: AudioQuality.HIGH,
                    sampleRate: 44100,
                    numberOfChannels: 2,
                    bitRate: 128000,
                    linearPCMBitDepth: 16,
                    linearPCMIsBigEndian: false,
                    linearPCMIsFloat: false,
                },
            });

            this.recordRecorder.record();

            this.recordRecorder.addListener('recordingStatusUpdate', (status: RecorderState) => {
                this.recordSecs = status.durationMillis / 1000;
                this.recordTime = this.formatTime(Math.floor(this.recordSecs));
            });

            console.log(`Gravação iniciada com expo-audio`);
        }
    } catch (error) {
        console.error('Erro ao iniciar gravação:', error);
    }
}

    formatTime(seconds: number): string {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    async onStop() {
        if (this.pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
            const result = await this.recordPLugin.stopRecorder();
            this.recordPLugin.removeRecordBackListener();
            this.recordSecs = 0;
            console.log(`result: ${result}`);
        } else if (this.pluginType === PluginType.EXPO_AUDIO) {
            try {
                const result = await this.recordRecorder.stop();

                this.recordRecorder.removeAllListeners();

                this.recordSecs = 0;
                this.recordTime = '00:00';

                console.log(`Recording stopped: ${result}`);

                if (this.recordRecorder.uri) {
                    this.path = this.recordRecorder.uri;
                }

                // Deactivate audio session when not recording
                if (Platform.OS === 'ios') {
                    await setIsAudioActiveAsync(false);
                }
            } catch (error) {
                console.error('Error stopping recording:', error);
            }
        }
    }

    async onPlay() {
        if (this.pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
            const msg = await this.recordPLugin.startPlayer(this.path);
            const volume = await this.recordPLugin.setVolume(1.0);
            console.log(`msg: ${msg}, volume: ${volume}`);
            this.recordPLugin.addPlayBackListener((e: PlayBackType) => {
                    this.currentPositionSec = e.currentPosition;
                    this.currentDurationSec =  e.duration;
                    this.playTime = this.recordPLugin.mmssss((Math.floor(e.currentPosition)));
                    this.duration = this.recordPLugin.mmssss((Math.floor(e.duration)));
            });
        } else if (this.pluginType === PluginType.EXPO_AUDIO) {
            try {
                if (!this.path) {
                    console.log('No recording to play');
                    return;
                }

                // Configure audio session for playback
                if (Platform.OS === 'ios') {
                    await setAudioModeAsync({
                        playsInSilentMode: true,
                        interruptionMode: 'mixWithOthers',
                        allowsRecording: false,
                        shouldPlayInBackground: false
                    });

                    // Activate audio session
                    await setIsAudioActiveAsync(true);
                }

                this.recordPLugin.replace({ uri: this.path });

                this.recordPLugin.volume = 1.0;

                this.recordPLugin.play();

                this.recordPLugin.addListener('playbackStatusUpdate', (status: AudioStatus) => {
                    this.currentPositionSec = status.currentTime * 1000;
                    this.currentDurationSec = status.duration * 1000;
                    this.playTime = this.formatTime(Math.floor(status.currentTime));
                    this.duration = this.formatTime(Math.floor(status.duration));

                    // Deactivate audio session when playback finishes
                    if (status.didJustFinish && Platform.OS === 'ios') {
                        setIsAudioActiveAsync(false).catch(err => 
                            console.error('Error deactivating audio session:', err)
                        );
                    }
                });

                console.log(`Playing audio with expo-audio: ${this.path}`);
            } catch (error) {
                console.error('Error playing audio:', error);
            }
        }
    }
    async getPlayList() {
        if (this.pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
            const list = await this.recordPLugin.getPlayList();
            console.log(`list: ${list}`);
            return list;
        }
    }

    async cleanup() {
        try {
            if (this.pluginType === PluginType.EXPO_AUDIO) {
                // Stop any ongoing recording
                if (this.recordRecorder && this.recordRecorder.isRecording) {
                    await this.recordRecorder.stop();
                    this.recordRecorder.removeAllListeners();
                }

                // Stop any ongoing playback
                if (this.recordPLugin && this.recordPLugin.playing) {
                    this.recordPLugin.pause();
                    this.recordPLugin.removeAllListeners();
                }

                // Deactivate audio session
                if (Platform.OS === 'ios') {
                    await setIsAudioActiveAsync(false);
                }

                console.log('Audio resources cleaned up');
            }
        } catch (error) {
            console.error('Error cleaning up audio resources:', error);
        }
    }

}

export default RecordProvider;
