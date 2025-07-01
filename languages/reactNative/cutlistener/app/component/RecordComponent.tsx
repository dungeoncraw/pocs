import {Button, Text, SafeAreaView, Platform} from "react-native";
import AudioRecorderPlayer, {
    AudioEncoderAndroidType,
    AudioSet,
    AudioSourceAndroidType,
    AVEncoderAudioQualityIOSType, AVEncodingOption, OutputFormatAndroidType, RecordBackType
} from "react-native-audio-recorder-player";
import {useState} from "react";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";

export enum PluginType {
    REACT_NATIVE_AUDIO_RECORDER_PLAYER = "RECORDER_PLAYER",
}

type IProps = {
    text: string;
    pluginType: PluginType;
}

export default function RecordComponent ({text, pluginType}: IProps) {
    const [recordSecs, setRecordSecs] = useState({recordSecs: 0, recordTime: '00:00'});
    console.log('recordSecs', recordSecs);
    const onPressRecord = async () => {
        if (pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
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
            const path = Platform.select({
                ios: undefined,
                android: undefined
            })
            const audioRecordPLayer = new AudioRecorderPlayer();
            const uri = await audioRecordPLayer.startRecorder(
                path,
                audioSet,
            );

            audioRecordPLayer.addRecordBackListener((e: RecordBackType) => {
                setRecordSecs({
                    recordSecs: e.currentPosition,
                    recordTime: audioRecordPLayer.mmssss(
                        Math.floor(e.currentPosition),
                    ),
                });
            });
            console.log(`uri: ${uri}`);
        }
    };
    return(
        <SafeAreaView>
            <Text>{text}</Text>
            <Button title="Start Recording" onPress={onPressRecord}/>
        </SafeAreaView>
    )
}