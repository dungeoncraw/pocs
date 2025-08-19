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
import {Platform} from "react-native";
import * as FileSystem from 'expo-file-system';
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import {PluginType, Recordings} from "@/app/types/types";
import SequentialAudioPlayer from "@/app/helper/sequentialPlayer";

class RecordProvider {
    pluginType: PluginType;
    recordPlugin: any;
    path: string | undefined;
    recordSecs = 0;
    recordTime = '00:00';
    currentPositionSec = 0;
    currentDurationSec = 0;
    playTime = '00:00';
    duration = '00:00';
    isRecording = false;
    private readonly RECORDINGS_DIRECTORY = `${FileSystem.documentDirectory}cutlistener/recordings/`;
    private readonly RECORDING_EXTENSION = '.m4a';

    private async ensureDirectoryExists() {
        const dirInfo = await FileSystem.getInfoAsync(this.RECORDINGS_DIRECTORY);
        if (!dirInfo.exists) {
            await FileSystem.makeDirectoryAsync(this.RECORDINGS_DIRECTORY, {intermediates: true});
        }
    }

    constructor(pluginType: PluginType, path?: string) {
        this.pluginType = pluginType;
        this.ensureDirectoryExists().catch(console.error);
        if (!path) {
            const timestamp = new Date().getTime();
            this.path = `${this.RECORDINGS_DIRECTORY}recording_${timestamp}${this.RECORDING_EXTENSION}`;
        } else {
            this.path = path;
        }
        switch (pluginType) {
            case PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER:
                this.recordPlugin = new AudioRecorderPlayer();
                break;
        }
    }

    async onRecord() {
        try {
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
            const uri = await this.recordPlugin.startRecorder(
                this.path,
                audioSet,
            );

            this.recordPlugin.addRecordBackListener(async (e: RecordBackType) => {
                this.recordSecs = e.currentPosition;
                this.recordTime = this.recordPlugin.mmssss(Math.floor(e.currentPosition));
                // here's the tricky, need to play a count time over the record, so user known which set he is
                // first count from one to ten, with 10 seconds between each number count, so can record the draw of sword
            });

            this.isRecording = true;
            console.log(`Recording with RN Audio Recorder: ${uri}`);
        } catch (error) {
            console.error('Error on recording:', error);
        }
    }

    async onStop() {
        const result = await this.recordPlugin.stopRecorder();
        this.recordPlugin.removeRecordBackListener();
        this.recordSecs = 0;
        this.isRecording = false;
        if (result && result !== this.path) {
            const newPath = `${this.RECORDINGS_DIRECTORY}recording_${Date.now()}${this.RECORDING_EXTENSION}`;
            await FileSystem.moveAsync({
                from: result,
                to: newPath
            });
            this.path = newPath;
        }
        console.log(`record saved at: ${this.path}`);
    }

    async onPlay(recordPath?: string) {
        const msg = await this.recordPlugin.startPlayer(recordPath ?? this.path);
        const volume = await this.recordPlugin.setVolume(1.0);
        console.log(`msg: ${msg}, volume: ${volume}`);
        this.recordPlugin.addPlayBackListener((e: PlayBackType) => {
            this.currentPositionSec = e.currentPosition;
            this.currentDurationSec = e.duration;
            this.playTime = this.recordPlugin.mmssss((Math.floor(e.currentPosition)));
            this.duration = this.recordPlugin.mmssss((Math.floor(e.duration)));
        });
    }

    async removeRecord(recordPath: string) {
        try {
            await FileSystem.deleteAsync(recordPath);
            console.log(`Record deleted: ${recordPath}`);
        } catch (error) {
            console.error('Error deleting record:', error);
        }
    }

    async getPlayList() {
        try {
            await this.ensureDirectoryExists();

            if (Platform.OS === 'android') {
                const nativeList = await this.recordPlugin.getPlayList() || [];
                const filesInDirectory = await this.getRecordingsFromDirectory();
                return [...new Set([...nativeList, ...filesInDirectory])];
            } else {
                return await this.getRecordingsFromDirectory();
            }
        } catch (error) {
            console.error('Error getting playlist:', error);
            return [];
        }
    }

    async cleanup() {
        // No cleanup needed for react-native-audio-recorder-player
    }

    private async getRecordingsFromDirectory(): Promise<Recordings[]> {
        try {
            const files = await FileSystem.readDirectoryAsync(this.RECORDINGS_DIRECTORY);
            const recordings = files
                .filter(file => file.endsWith(this.RECORDING_EXTENSION))
                .map(async (file) => {
                    const uri = `${this.RECORDINGS_DIRECTORY}${file}`;
                    const fileInfo = await FileSystem.getInfoAsync(uri);
                    return {
                        uri: fileInfo.uri,
                        name: file,
                    };
                });

            return await Promise.all(recordings);
        } catch (error) {
            console.error('Error reading recordings directory:', error);
            return [];
        }
    }


}

export default RecordProvider;
