import { useState, useEffect, useCallback, useRef } from 'react';
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
import {
    createAudioPlayer,
    AudioModule,
    requestRecordingPermissionsAsync,
    AudioStatus,
    RecorderState,
    AudioQuality,
    IOSOutputFormat,
    setAudioModeAsync,
    setIsAudioActiveAsync
} from "expo-audio";
import { Platform } from "react-native";
import * as FileSystem from 'expo-file-system';
import { requestMicrophonePermission } from "@/app/helper/requestPermission";
import { PluginType, Recordings } from "@/app/types/types";

interface UseRecordProviderReturn {
    isRecording: boolean;
    recordSecs: number;
    recordTime: string;
    currentPositionSec: number;
    currentDurationSec: number;
    playTime: string;
    duration: string;
    onRecord: () => Promise<void>;
    onStop: () => Promise<void>;
    onPlay: (recordPath?: string) => Promise<void>;
    removeRecord: (recordPath: string) => Promise<void>;
    getPlayList: () => Promise<Recordings[]>;
    cleanup: () => Promise<void>;
}

interface UseRecordProviderOptions {
    pluginType?: PluginType;
    initialPath?: string;
}

export default function useRecordProvider(options: UseRecordProviderOptions = {}): UseRecordProviderReturn {
    const { pluginType = PluginType.EXPO_AUDIO, initialPath } = options;
    
    // State management
    const [isRecording, setIsRecording] = useState(false);
    const [recordSecs, setRecordSecs] = useState(0);
    const [recordTime, setRecordTime] = useState('00:00');
    const [currentPositionSec, setCurrentPositionSec] = useState(0);
    const [currentDurationSec, setCurrentDurationSec] = useState(0);
    const [playTime, setPlayTime] = useState('00:00');
    const [duration, setDuration] = useState('00:00');
    const [path, setPath] = useState<string | undefined>(initialPath);
    
    // Refs for plugin instances
    const recordPluginRef = useRef<any>(null);
    const recordRecorderRef = useRef<any>(null);
    const previousPluginTypeRef = useRef<PluginType | null>(null);
    const isCleaningUpRef = useRef<boolean>(false);
    
    // Constants
    const RECORDINGS_DIRECTORY = `${FileSystem.documentDirectory}cutlistener/recordings/`;
    const RECORDING_EXTENSION = '.m4a';
    
    // Utility function
    const formatTime = useCallback((seconds: number): string => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }, []);
    
    // Ensure directory exists
    const ensureDirectoryExists = useCallback(async () => {
        const dirInfo = await FileSystem.getInfoAsync(RECORDINGS_DIRECTORY);
        if (!dirInfo.exists) {
            await FileSystem.makeDirectoryAsync(RECORDINGS_DIRECTORY, { intermediates: true });
        }
    }, [RECORDINGS_DIRECTORY]);
    
    // Initialize plugins
    useEffect(() => {
        const initializePlugins = async () => {
            await ensureDirectoryExists();
            
            if (!path) {
                const timestamp = new Date().getTime();
                setPath(`${RECORDINGS_DIRECTORY}recording_${timestamp}${RECORDING_EXTENSION}`);
            }
            
            switch (pluginType) {
                case PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER:
                    recordPluginRef.current = new AudioRecorderPlayer();
                    break;
                case PluginType.EXPO_AUDIO:
                    // Configure audio session for recording
                    if (Platform.OS === 'ios') {
                        await setAudioModeAsync({
                            playsInSilentMode: true,
                            interruptionMode: 'mixWithOthers',
                            allowsRecording: true,
                            shouldPlayInBackground: false
                        });
                    } else {
                        await setAudioModeAsync({
                            interruptionModeAndroid: 'duckOthers',
                            shouldPlayInBackground: false,
                            shouldRouteThroughEarpiece: false
                        });
                    }
                    
                    // Activate audio session
                    await setIsAudioActiveAsync(true);
                    
                    recordPluginRef.current = createAudioPlayer();
                    recordRecorderRef.current = new (AudioModule as any).AudioRecorder({});
                    break;
            }
        };
        
        const cleanupPlugins = async () => {
            // Prevent race conditions - only one cleanup at a time
            if (isCleaningUpRef.current) {
                console.log('Cleanup already in progress, skipping...');
                return;
            }
            
            isCleaningUpRef.current = true;
            
            try {
                console.log('Starting plugin cleanup...');
                
                // Stop any ongoing recording
                if (isRecording) {
                    setIsRecording(false);
                }
                
                // Use PREVIOUS plugin type for cleanup, not the new one
                const previousPluginType = previousPluginTypeRef.current;
                console.log(`Cleaning up previous plugin type: ${previousPluginType}`);
                
                // Cleanup audio player based on PREVIOUS plugin type
                if (recordPluginRef.current && previousPluginType) {
                    if (previousPluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
                        console.log('Cleaning up RN Audio Recorder Player...');
                        // Stop any ongoing recording or playback for RN Audio
                        try {
                            if (typeof recordPluginRef.current.stopRecorder === 'function') {
                                await recordPluginRef.current.stopRecorder();
                                console.log('✓ RN Audio recorder stopped');
                            }
                        } catch (e: any) {
                            console.log('RN Audio recorder was not recording:', e.message);
                        }
                        
                        try {
                            if (typeof recordPluginRef.current.removeRecordBackListener === 'function') {
                                recordPluginRef.current.removeRecordBackListener();
                                console.log('✓ RN Audio record listeners removed');
                            }
                        } catch (e: any) {
                            console.log('Error removing RN Audio record listeners:', e.message);
                        }
                        
                        try {
                            if (typeof recordPluginRef.current.stopPlayer === 'function') {
                                await recordPluginRef.current.stopPlayer();
                                console.log('✓ RN Audio player stopped');
                            }
                        } catch (e: any) {
                            console.log('RN Audio player was not playing:', e.message);
                        }
                        
                        try {
                            if (typeof recordPluginRef.current.removePlayBackListener === 'function') {
                                recordPluginRef.current.removePlayBackListener();
                                console.log('✓ RN Audio playback listeners removed');
                            }
                        } catch (e: any) {
                            console.log('Error removing RN Audio playback listeners:', e.message);
                        }
                        
                    } else if (previousPluginType === PluginType.EXPO_AUDIO) {
                        console.log('Cleaning up Expo Audio Player...');
                        // Stop any ongoing playback for Expo Audio
                        try {
                            if (recordPluginRef.current && typeof recordPluginRef.current.pause === 'function') {
                                if (recordPluginRef.current.playing) {
                                    recordPluginRef.current.pause();
                                    console.log('✓ Expo Audio player paused');
                                }
                            }
                        } catch (e: any) {
                            console.log('Error pausing Expo Audio player:', e.message);
                        }
                        
                        try {
                            if (recordPluginRef.current && typeof recordPluginRef.current.removeAllListeners === 'function') {
                                recordPluginRef.current.removeAllListeners();
                                console.log('✓ Expo Audio player listeners removed');
                            }
                        } catch (e: any) {
                            console.log('Error removing Expo Audio player listeners:', e.message);
                        }
                    }
                }
                
                // Cleanup Expo Audio recorder (only exists for Expo Audio)
                if (recordRecorderRef.current && previousPluginType === PluginType.EXPO_AUDIO) {
                    console.log('Cleaning up Expo Audio Recorder...');
                    try {
                        if (recordRecorderRef.current.isRecording && typeof recordRecorderRef.current.stop === 'function') {
                            await recordRecorderRef.current.stop();
                            console.log('✓ Expo Audio recorder stopped');
                        }
                    } catch (e: any) {
                        console.log('Error stopping Expo Audio recorder:', e.message);
                    }
                    
                    try {
                        if (typeof recordRecorderRef.current.removeAllListeners === 'function') {
                            recordRecorderRef.current.removeAllListeners();
                            console.log('✓ Expo Audio recorder listeners removed');
                        }
                    } catch (e: any) {
                        console.log('Error removing Expo Audio recorder listeners:', e.message);
                    }
                }
                
                // Deactivate audio session for Expo Audio on iOS
                if (previousPluginType === PluginType.EXPO_AUDIO && Platform.OS === 'ios') {
                    try {
                        await Promise.race([
                            setIsAudioActiveAsync(false),
                            new Promise((_, reject) => setTimeout(() => reject(new Error('Audio session timeout')), 3000))
                        ]);
                        console.log('✓ iOS audio session deactivated');
                    } catch (e: any) {
                        console.log('Error deactivating iOS audio session:', e.message);
                    }
                }
                
                // Reset refs safely
                recordPluginRef.current = null;
                recordRecorderRef.current = null;
                
                // Reset state
                setRecordSecs(0);
                setRecordTime('00:00');
                setCurrentPositionSec(0);
                setCurrentDurationSec(0);
                setPlayTime('00:00');
                setDuration('00:00');
                
                console.log('✅ Plugin cleanup completed successfully');
                
            } catch (error) {
                console.error('❌ Critical error during plugin cleanup:', error);
                // Even if cleanup fails, reset refs to prevent further issues
                recordPluginRef.current = null;
                recordRecorderRef.current = null;
            } finally {
                isCleaningUpRef.current = false;
            }
        };
        
        // Cleanup previous plugins before initializing new ones
        cleanupPlugins().then(() => {
            initializePlugins().then(() => {
                // Update the previous plugin type reference AFTER successful initialization
                previousPluginTypeRef.current = pluginType;
                console.log(`Previous plugin type set to: ${pluginType}`);
            }).catch(console.error);
        });
        
        // Return cleanup function for useEffect
        return () => {
            cleanupPlugins();
        };
    }, [pluginType, ensureDirectoryExists, path, RECORDINGS_DIRECTORY, RECORDING_EXTENSION, isRecording]);
    
    // Record function
    const onRecord = useCallback(async () => {
        try {
            // Prevent recording if cleanup is in progress
            if (isCleaningUpRef.current) {
                console.log('Cannot start recording: cleanup in progress');
                return;
            }
            
            setIsRecording(true);
            
            if (pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
                // Defensive check for plugin instance
                if (!recordPluginRef.current) {
                    console.error('RN Audio Recorder plugin not initialized');
                    setIsRecording(false);
                    return;
                }
                
                if (Platform.OS === 'android') {
                    try {
                        const hasPermissions = await requestMicrophonePermission();
                        if (!hasPermissions) {
                            console.log('Permission not granted');
                            setIsRecording(false);
                            return;
                        }
                    } catch (err) {
                        console.warn('Permission error:', err);
                        setIsRecording(false);
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
                
                try {
                    // Defensive check before calling startRecorder
                    if (typeof recordPluginRef.current.startRecorder !== 'function') {
                        throw new Error('startRecorder method not available');
                    }
                    
                    const uri = await recordPluginRef.current.startRecorder(path, audioSet);
                    
                    // Defensive check before adding listener
                    if (typeof recordPluginRef.current.addRecordBackListener === 'function') {
                        recordPluginRef.current.addRecordBackListener((e: RecordBackType) => {
                            setRecordSecs(e.currentPosition);
                            // Defensive check for mmssss method
                            if (recordPluginRef.current && typeof recordPluginRef.current.mmssss === 'function') {
                                setRecordTime(recordPluginRef.current.mmssss(Math.floor(e.currentPosition)));
                            } else {
                                setRecordTime(formatTime(Math.floor(e.currentPosition / 1000)));
                            }
                        });
                    }
                    
                    console.log(`✓ Recording started with RN Audio Recorder: ${uri}`);
                } catch (err) {
                    console.error('Error starting RN Audio recording:', err);
                    setIsRecording(false);
                    return;
                }
                
            } else if (pluginType === PluginType.EXPO_AUDIO) {
                // Defensive check for recorder instance
                if (!recordRecorderRef.current) {
                    console.error('Expo Audio Recorder not initialized');
                    setIsRecording(false);
                    return;
                }
                
                if (Platform.OS === 'android') {
                    try {
                        const permissionResponse = await requestRecordingPermissionsAsync();
                        if (!permissionResponse.granted) {
                            console.log('Permission not granted');
                            setIsRecording(false);
                            return;
                        }
                    } catch (err) {
                        console.warn('Permission error:', err);
                        setIsRecording(false);
                        return;
                    }
                } else if (Platform.OS === 'ios') {
                    try {
                        await setAudioModeAsync({
                            playsInSilentMode: true,
                            interruptionMode: 'mixWithOthers',
                            allowsRecording: true,
                            shouldPlayInBackground: false
                        });
                        await setIsAudioActiveAsync(true);
                    } catch (err) {
                        console.error('Error configuring audio session:', err);
                        setIsRecording(false);
                        return;
                    }
                }
                
                try {
                    // Defensive check before calling prepareToRecordAsync
                    if (typeof recordRecorderRef.current.prepareToRecordAsync !== 'function') {
                        throw new Error('prepareToRecordAsync method not available');
                    }
                    
                    await recordRecorderRef.current.prepareToRecordAsync({
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
                    
                    // Defensive check before calling record
                    if (typeof recordRecorderRef.current.record !== 'function') {
                        throw new Error('record method not available');
                    }
                    
                    recordRecorderRef.current.record();
                    
                    // Defensive check before adding listener
                    if (typeof recordRecorderRef.current.addListener === 'function') {
                        recordRecorderRef.current.addListener('recordingStatusUpdate', (status: RecorderState) => {
                            setRecordSecs(status.durationMillis / 1000);
                            setRecordTime(formatTime(Math.floor(status.durationMillis / 1000)));
                        });
                    }
                    
                    console.log(`✓ Recording started with expo-audio`);
                } catch (err) {
                    console.error('Error starting Expo Audio recording:', err);
                    setIsRecording(false);
                    return;
                }
            }
        } catch (error) {
            console.error('❌ Critical error in onRecord:', error);
            setIsRecording(false);
        }
    }, [pluginType, path, formatTime]);
    
    // Stop function
    const onStop = useCallback(async () => {
        try {
            setIsRecording(false);
            
            if (pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
                // Defensive check for plugin instance
                if (!recordPluginRef.current) {
                    console.error('RN Audio Recorder plugin not available for stopping');
                    return;
                }
                
                try {
                    let result = null;
                    
                    // Defensive check before calling stopRecorder
                    if (typeof recordPluginRef.current.stopRecorder === 'function') {
                        result = await recordPluginRef.current.stopRecorder();
                        console.log('✓ RN Audio recorder stopped');
                    } else {
                        console.warn('stopRecorder method not available');
                    }
                    
                    // Defensive check before removing listener
                    if (typeof recordPluginRef.current.removeRecordBackListener === 'function') {
                        recordPluginRef.current.removeRecordBackListener();
                        console.log('✓ RN Audio record listeners removed');
                    } else {
                        console.warn('removeRecordBackListener method not available');
                    }
                    
                    setRecordSecs(0);
                    setRecordTime('00:00');
                    
                    // Handle file moving if result path is different
                    if (result && result !== path) {
                        try {
                            const newPath = `${RECORDINGS_DIRECTORY}recording_${Date.now()}${RECORDING_EXTENSION}`;
                            await FileSystem.moveAsync({
                                from: result,
                                to: newPath
                            });
                            setPath(newPath);
                            console.log(`✓ Recording moved to: ${newPath}`);
                        } catch (moveError) {
                            console.error('Error moving recording file:', moveError);
                        }
                    }
                    
                    console.log(`✓ RN Audio recording saved at: ${path || result}`);
                } catch (err) {
                    console.error('Error stopping RN Audio recording:', err);
                }
                
            } else if (pluginType === PluginType.EXPO_AUDIO) {
                // Defensive check for recorder instance
                if (!recordRecorderRef.current) {
                    console.error('Expo Audio Recorder not available for stopping');
                    return;
                }
                
                try {
                    let result = null;
                    
                    // Defensive check before calling stop
                    if (typeof recordRecorderRef.current.stop === 'function') {
                        result = await recordRecorderRef.current.stop();
                        console.log('✓ Expo Audio recorder stopped');
                    } else {
                        console.warn('stop method not available on Expo Audio recorder');
                    }
                    
                    // Defensive check before removing listeners
                    if (typeof recordRecorderRef.current.removeAllListeners === 'function') {
                        recordRecorderRef.current.removeAllListeners();
                        console.log('✓ Expo Audio recorder listeners removed');
                    } else {
                        console.warn('removeAllListeners method not available on Expo Audio recorder');
                    }
                    
                    setRecordSecs(0);
                    setRecordTime('00:00');
                    
                    console.log(`✓ Expo Audio recording stopped: ${result}`);
                    
                    // Handle file moving if recorder has uri
                    if (recordRecorderRef.current && recordRecorderRef.current.uri) {
                        try {
                            const newPath = `${RECORDINGS_DIRECTORY}recording_${Date.now()}${RECORDING_EXTENSION}`;
                            await FileSystem.moveAsync({
                                from: recordRecorderRef.current.uri,
                                to: newPath
                            });
                            setPath(newPath);
                            console.log(`✓ Expo Audio recording moved to: ${newPath}`);
                        } catch (moveError) {
                            console.error('Error moving Expo Audio recording file:', moveError);
                        }
                    }
                    
                    // Deactivate audio session on iOS
                    if (Platform.OS === 'ios') {
                        try {
                            await Promise.race([
                                setIsAudioActiveAsync(false),
                                new Promise((_, reject) => setTimeout(() => reject(new Error('Audio session timeout')), 3000))
                            ]);
                            console.log('✓ iOS audio session deactivated');
                        } catch (audioError) {
                            console.error('Error deactivating iOS audio session:', audioError);
                        }
                    }
                    
                } catch (error) {
                    console.error('Error stopping Expo Audio recording:', error);
                }
            }
        } catch (error) {
            console.error('❌ Critical error in onStop:', error);
        }
    }, [pluginType, path, RECORDINGS_DIRECTORY, RECORDING_EXTENSION]);
    
    // Play function
    const onPlay = useCallback(async (recordPath?: string) => {
        try {
            // Prevent playback if cleanup is in progress
            if (isCleaningUpRef.current) {
                console.log('Cannot start playback: cleanup in progress');
                return;
            }
            
            if (pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
                // Defensive check for plugin instance
                if (!recordPluginRef.current) {
                    console.error('RN Audio Recorder plugin not available for playback');
                    return;
                }
                
                const playPath = recordPath ?? path;
                if (!playPath) {
                    console.log('No recording path provided for RN Audio playback');
                    return;
                }
                
                try {
                    let msg = null;
                    let volume = null;
                    
                    // Defensive check before calling startPlayer
                    if (typeof recordPluginRef.current.startPlayer === 'function') {
                        msg = await recordPluginRef.current.startPlayer(playPath);
                        console.log('✓ RN Audio player started');
                    } else {
                        console.error('startPlayer method not available');
                        return;
                    }
                    
                    // Defensive check before setting volume
                    if (typeof recordPluginRef.current.setVolume === 'function') {
                        volume = await recordPluginRef.current.setVolume(1.0);
                        console.log('✓ RN Audio volume set');
                    } else {
                        console.warn('setVolume method not available');
                    }
                    
                    console.log(`✓ RN Audio playback started - msg: ${msg}, volume: ${volume}`);
                    
                    // Defensive check before adding playback listener
                    if (typeof recordPluginRef.current.addPlayBackListener === 'function') {
                        recordPluginRef.current.addPlayBackListener((e: PlayBackType) => {
                            setCurrentPositionSec(e.currentPosition);
                            setCurrentDurationSec(e.duration);
                            
                            // Defensive check for mmssss method
                            if (recordPluginRef.current && typeof recordPluginRef.current.mmssss === 'function') {
                                setPlayTime(recordPluginRef.current.mmssss(Math.floor(e.currentPosition)));
                                setDuration(recordPluginRef.current.mmssss(Math.floor(e.duration)));
                            } else {
                                setPlayTime(formatTime(Math.floor(e.currentPosition / 1000)));
                                setDuration(formatTime(Math.floor(e.duration / 1000)));
                            }
                        });
                        console.log('✓ RN Audio playback listener added');
                    } else {
                        console.warn('addPlayBackListener method not available');
                    }
                    
                } catch (err) {
                    console.error('Error starting RN Audio playback:', err);
                }
                
            } else if (pluginType === PluginType.EXPO_AUDIO) {
                // Defensive check for plugin instance
                if (!recordPluginRef.current) {
                    console.error('Expo Audio Player not available for playback');
                    return;
                }
                
                const playPath = recordPath ?? path;
                if (!playPath) {
                    console.log('No recording path provided for Expo Audio playback');
                    return;
                }
                
                try {
                    // Configure audio session for iOS
                    if (Platform.OS === 'ios') {
                        try {
                            await setAudioModeAsync({
                                playsInSilentMode: true,
                                interruptionMode: 'mixWithOthers',
                                allowsRecording: false,
                                shouldPlayInBackground: false
                            });
                            
                            await Promise.race([
                                setIsAudioActiveAsync(true),
                                new Promise((_, reject) => setTimeout(() => reject(new Error('Audio session timeout')), 3000))
                            ]);
                            console.log('✓ iOS audio session configured for playback');
                        } catch (audioError) {
                            console.error('Error configuring iOS audio session for playback:', audioError);
                            return;
                        }
                    }
                    
                    // Defensive check before calling replace
                    if (typeof recordPluginRef.current.replace === 'function') {
                        recordPluginRef.current.replace({ uri: playPath });
                        console.log('✓ Expo Audio source replaced');
                    } else {
                        console.error('replace method not available on Expo Audio player');
                        return;
                    }
                    
                    // Defensive check before setting volume
                    if (recordPluginRef.current.hasOwnProperty('volume')) {
                        recordPluginRef.current.volume = 1.0;
                        console.log('✓ Expo Audio volume set');
                    } else {
                        console.warn('volume property not available on Expo Audio player');
                    }
                    
                    // Defensive check before calling play
                    if (typeof recordPluginRef.current.play === 'function') {
                        recordPluginRef.current.play();
                        console.log('✓ Expo Audio playback started');
                    } else {
                        console.error('play method not available on Expo Audio player');
                        return;
                    }
                    
                    // Defensive check before adding listener
                    if (typeof recordPluginRef.current.addListener === 'function') {
                        recordPluginRef.current.addListener('playbackStatusUpdate', (status: AudioStatus) => {
                            setCurrentPositionSec(status.currentTime * 1000);
                            setCurrentDurationSec(status.duration * 1000);
                            setPlayTime(formatTime(Math.floor(status.currentTime)));
                            setDuration(formatTime(Math.floor(status.duration)));
                            
                            if (status.didJustFinish && Platform.OS === 'ios') {
                                setIsAudioActiveAsync(false).catch(err =>
                                    console.error('Error deactivating audio session:', err)
                                );
                            }
                        });
                        console.log('✓ Expo Audio playback listener added');
                    } else {
                        console.warn('addListener method not available on Expo Audio player');
                    }
                    
                    console.log(`✓ Expo Audio playback started: ${playPath}`);
                    
                } catch (err) {
                    console.error('Error starting Expo Audio playback:', err);
                }
            }
        } catch (error) {
            console.error('❌ Critical error in onPlay:', error);
        }
    }, [pluginType, path, formatTime]);
    
    // Remove record function
    const removeRecord = useCallback(async (recordPath: string) => {
        try {
            await FileSystem.deleteAsync(recordPath);
            console.log(`Record deleted: ${recordPath}`);
        } catch (error) {
            console.error('Error deleting record:', error);
        }
    }, []);
    
    // Get recordings from directory
    const getRecordingsFromDirectory = useCallback(async (): Promise<Recordings[]> => {
        try {
            const files = await FileSystem.readDirectoryAsync(RECORDINGS_DIRECTORY);
            const recordings = files
                .filter(file => file.endsWith(RECORDING_EXTENSION))
                .map(async (file) => {
                    const uri = `${RECORDINGS_DIRECTORY}${file}`;
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
    }, [RECORDINGS_DIRECTORY, RECORDING_EXTENSION]);
    
    // Get playlist function
    const getPlayList = useCallback(async (): Promise<Recordings[]> => {
        try {
            await ensureDirectoryExists();
            
            if (pluginType === PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER) {
                if (Platform.OS === 'android') {
                    const nativeList = await recordPluginRef.current.getPlayList() || [];
                    const filesInDirectory = await getRecordingsFromDirectory();
                    return [...new Set([...nativeList, ...filesInDirectory])];
                } else {
                    return await getRecordingsFromDirectory();
                }
            } else if (pluginType === PluginType.EXPO_AUDIO) {
                return await getRecordingsFromDirectory();
            }
            
            return [];
        } catch (error) {
            console.error('Error getting playlist:', error);
            return [];
        }
    }, [pluginType, ensureDirectoryExists, getRecordingsFromDirectory]);
    
    // Cleanup function
    const cleanup = useCallback(async () => {
        try {
            if (pluginType === PluginType.EXPO_AUDIO) {
                if (recordRecorderRef.current && recordRecorderRef.current.isRecording) {
                    await recordRecorderRef.current.stop();
                    recordRecorderRef.current.removeAllListeners();
                }
                
                if (recordPluginRef.current && recordPluginRef.current.playing) {
                    recordPluginRef.current.pause();
                    recordPluginRef.current.removeAllListeners();
                }
                
                if (Platform.OS === 'ios') {
                    await setIsAudioActiveAsync(false);
                }
                
                console.log('Audio resources cleaned up');
            }
        } catch (error) {
            console.error('Error cleaning up audio resources:', error);
        }
    }, [pluginType]);
    
    // Cleanup on unmount
    useEffect(() => {
        return () => {
            cleanup();
        };
    }, [cleanup]);
    
    return {
        isRecording,
        recordSecs,
        recordTime,
        currentPositionSec,
        currentDurationSec,
        playTime,
        duration,
        onRecord,
        onStop,
        onPlay,
        removeRecord,
        getPlayList,
        cleanup
    };
}