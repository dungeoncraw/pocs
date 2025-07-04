import {Button, Text, SafeAreaView, Platform, StyleSheet} from "react-native";
import AudioRecorderPlayer, {
    AudioEncoderAndroidType,
    AudioSet,
    AudioSourceAndroidType,
    AVEncoderAudioQualityIOSType, AVEncodingOption, OutputFormatAndroidType, PlayBackType, RecordBackType
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

export default function RecordComponent({text, pluginType}: IProps) {
    const [recordSecs, setRecordSecs] = useState<{ recordSecs: number, recordTime: string }>({
        recordSecs: 0,
        recordTime: '00:00'
    });
    const [playbackMeta, setPlaybackMeta] = useState<{
        currentPositionSec: number,
        currentDurationSec: number,
        playTime: string,
        duration: string
    }>({currentPositionSec: 0, currentDurationSec: 0, playTime: '00:00', duration: '00:00'});
    // THIS NEEDS TO BE REFACTORED TO HANDLE NEW PLUGIN
    const audioRecordPLayer = new AudioRecorderPlayer();
    const path = Platform.select({
        ios: undefined,
        android: undefined
    });
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

    const onPressStop = async () => {
        const result = await audioRecordPLayer.stopRecorder();
        audioRecordPLayer.removeRecordBackListener();
        setRecordSecs({...recordSecs, recordSecs: 0});
        console.log(`result: ${result}`);
    }
    const onPressPlay = async () => {
        const msg = await audioRecordPLayer.startPlayer(path);
        const volume = await audioRecordPLayer.setVolume(1.0);
        console.log(`msg: ${msg}, volume: ${volume}`);
        audioRecordPLayer.addPlayBackListener((e: PlayBackType) => {
            setPlaybackMeta({
                currentPositionSec: e.currentPosition,
                currentDurationSec: e.duration,
                playTime: audioRecordPLayer.mmssss((Math.floor(e.currentPosition))),
                duration: audioRecordPLayer.mmssss((Math.floor(e.duration))),
            });
        });
    }
    return (
        <SafeAreaView style={styles.component}>
            <Text style={styles.subHeader}>{text}</Text>
            <Button color='#1ac10d' title="Start Recording" onPress={onPressRecord}/>
            <Button color='#c10d10' title="Stop Recording" onPress={onPressStop}/>
            <Button title="Play Recording" onPress={onPressPlay}/>
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    subHeader: {
        fontSize: 18,
        fontFamily: "Roboto",
    },
    component: {
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f9f6ea",
        borderColor: "#dceaf9",
        borderWidth: 2,
        borderRadius: 10,
        width: "100%",
    },
});