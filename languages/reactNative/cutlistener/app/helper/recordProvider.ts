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
import {createAudioPlayer} from "expo-audio";
import {Platform} from "react-native";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import {PluginType} from "@/app/types/types";

class RecordProvider {
    pluginType: PluginType;
    recordPLugin: any;
    path: string | undefined;
    recordSecs = 0;
    recordTime = '00:00';
    currentPositionSec= 0;
    currentDurationSec= 0;
    playTime=  '00:00';
    duration= '00:00';
    constructor(pluginType: PluginType, path: string | undefined) {
        this.pluginType = pluginType;
        this.path = path;
        switch (pluginType) {
            case PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER:
                this.recordPLugin = new AudioRecorderPlayer();
                break;
            case PluginType.EXPO_AUDIO:
                this.recordPLugin = createAudioPlayer();
        }
    }

    async onRecord() {
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
                    this.recordTime = this.recordPLugin.mmssss(
                        Math.floor(e.currentPosition),
                    );
            });
            console.log(`uri: ${uri}`);
        }
    }

    async onStop() {
        if (this.pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
            const result = await this.recordPLugin.stopRecorder();
            this.recordPLugin.removeRecordBackListener();
            this.recordSecs = 0;
            console.log(`result: ${result}`);
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
        }
    }

}

export default RecordProvider;