import { useState, useEffect, useRef, useCallback } from 'react';
import {
  createAudioPlayer,
  requestRecordingPermissionsAsync,
  AudioStatus,
  setAudioModeAsync,
  setIsAudioActiveAsync,
  useAudioRecorder,
  RecordingPresets
} from "expo-audio";
import { Platform } from "react-native";
import * as FileSystem from 'expo-file-system';
import { Recordings } from "@/app/types/types";

export const useRecordProvider = (initialPath?: string) => {
  const [recordSecs, setRecordSecs] = useState(0);
  const [recordTime, setRecordTime] = useState('00:00');
  const [currentPositionSec, setCurrentPositionSec] = useState(0);
  const [currentDurationSec, setCurrentDurationSec] = useState(0);
  const [playTime, setPlayTime] = useState('00:00');
  const [duration, setDuration] = useState('00:00');
  const [isRecording, setIsRecording] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [path, setPath] = useState<string | undefined>(initialPath);
  
  const recordPlayerRef = useRef<any>(null);
  const audioRecorder = useAudioRecorder(RecordingPresets.HIGH_QUALITY);
  
  const RECORDINGS_DIRECTORY = `${FileSystem.documentDirectory}cutlistener/recordings/`;
  const RECORDING_EXTENSION = '.m4a';

  const formatTime = useCallback((seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }, []);

  const safeCleanupPlayer = useCallback(async () => {
    if (recordPlayerRef.current) {
      try {
        if (recordPlayerRef.current.playing) {
          await recordPlayerRef.current.pause();
        }
        recordPlayerRef.current.removeAllListeners();
        setIsPlaying(false);
      } catch (error) {
        console.error('Error cleaning up player:', error);
        setIsPlaying(false);
      }
    }
  }, []);

  const ensureDirectoryExists = useCallback(async () => {
    const dirInfo = await FileSystem.getInfoAsync(RECORDINGS_DIRECTORY);
    if (!dirInfo.exists) {
      await FileSystem.makeDirectoryAsync(RECORDINGS_DIRECTORY, { intermediates: true });
    }
  }, [RECORDINGS_DIRECTORY]);

  const cleanupRef = useRef<() => Promise<void>>();

  const cleanup = useCallback(async () => {
    try {
      if (audioRecorder) {
        if (isRecording && audioRecorder.isRecording) {
          await audioRecorder.stopRecording();
          audioRecorder.removeRecordingStatusListener();
        }
        setIsRecording(false);
        setRecordSecs(0);
        setRecordTime('00:00');
      }
    } catch (recordingError) {
      console.error('Error cleaning up recording resources:', recordingError);
      setIsRecording(false);
      setRecordSecs(0);
      setRecordTime('00:00');
    }

    await safeCleanupPlayer();

    try {
      setCurrentPositionSec(0);
      setCurrentDurationSec(0);
      setPlayTime('00:00');
      setDuration('00:00');
    } catch (playbackError) {
      console.error('Error cleaning up playback resources:', playbackError);
      setIsPlaying(false);
      setCurrentPositionSec(0);
      setCurrentDurationSec(0);
      setPlayTime('00:00');
      setDuration('00:00');
    }

    try {
      if (Platform.OS === 'ios') {
        await setIsAudioActiveAsync(false);
      }
    } catch (audioSessionError) {
      console.error('Error deactivating audio session:', audioSessionError);
    }

    console.log('Audio resources cleaned up');
  }, [audioRecorder, safeCleanupPlayer, isRecording]);

  useEffect(() => {
    cleanupRef.current = cleanup;
  }, [cleanup]);
  
  useEffect(() => {
    const initializeAudio = async () => {
      try {
        await ensureDirectoryExists();
        
        if (!path) {
          const timestamp = new Date().getTime();
          setPath(`${RECORDINGS_DIRECTORY}recording_${timestamp}${RECORDING_EXTENSION}`);
        }
        
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
        
        await setIsAudioActiveAsync(true);
        
        recordPlayerRef.current = createAudioPlayer();
      } catch (error) {
        console.error('Error initializing audio:', error);
      }
    };

    initializeAudio();

    return () => {
      if (cleanupRef.current) {
        cleanupRef.current();
      }
    };
  }, [ensureDirectoryExists, path, RECORDINGS_DIRECTORY, RECORDING_EXTENSION]);

  const onRecord = useCallback(async () => {
    if (isRecording) {
      console.log('Already recording');
      return;
    }

    try {
      if (Platform.OS === 'android') {
        const permissionResponse = await requestRecordingPermissionsAsync();

        if (!permissionResponse.granted) {
          console.log('Permission not granted');
          return;
        }
      } 

      if (Platform.OS === 'ios') {
        await setAudioModeAsync({
          playsInSilentMode: true,
          interruptionMode: 'mixWithOthers',
          allowsRecording: true,
          shouldPlayInBackground: false
        });

        await setIsAudioActiveAsync(true);
      }

      if (!audioRecorder) {
        console.error('Audio recorder não está disponível');
        return;
      }

      await audioRecorder.startRecording();
      setIsRecording(true);

      audioRecorder.onRecordingStatusUpdate((status) => {
        setRecordSecs(status.durationMillis / 1000);
        setRecordTime(formatTime(Math.floor(status.durationMillis / 1000)));
      });

      console.log(`Recording with expo-audio`);
    } catch (error) {
      console.error('Error on recording:', error);
      setIsRecording(false);
      try {
        if (audioRecorder) {
          audioRecorder.removeRecordingStatusListener();
        }
      } catch (cleanupError) {
        console.error('Error cleaning up after recording failure:', cleanupError);
      }
    }
  }, [formatTime, audioRecorder, isRecording]);

  const onStop = useCallback(async () => {
    if (!isRecording || !audioRecorder) {
      console.log('Not recording or recorder not available');
      return;
    }

    try {
      const recording = await audioRecorder.stopRecording();
      audioRecorder.removeRecordingStatusListener();
      setRecordSecs(0);
      setRecordTime('00:00');
      setIsRecording(false);

      console.log(`Recording stopped: ${recording?.uri || 'No URI available'}`);

      if (recording && recording.uri) {
        await ensureDirectoryExists();
        const newPath = `${RECORDINGS_DIRECTORY}recording_${Date.now()}${RECORDING_EXTENSION}`;
        await FileSystem.moveAsync({
          from: recording.uri,
          to: newPath
        });
        setPath(newPath);
      }

      if (Platform.OS === 'ios') {
        await setIsAudioActiveAsync(false);
      }
    } catch (error) {
      console.error('Error stopping recording:', error);
      setIsRecording(false);
      setRecordSecs(0);
      setRecordTime('00:00');

      try {
        if (audioRecorder) {
          audioRecorder.removeRecordingStatusListener();
        }
      } catch (cleanupError) {
        console.error('Error cleaning up after stopping failure:', cleanupError);
      }

      if (Platform.OS === 'ios') {
        try {
          await setIsAudioActiveAsync(false);
        } catch (audioError) {
          console.error('Error deactivating audio session:', audioError);
        }
      }
    }
  }, [RECORDINGS_DIRECTORY, RECORDING_EXTENSION, audioRecorder, ensureDirectoryExists, isRecording]);

  const onPlay = useCallback(async (recordPath?: string) => {
    if (isPlaying) {
      console.log('Already playing audio');
      return;
    }

    try {
      const pathToPlay = recordPath || path;

      if (!pathToPlay) {
        console.log('No recording to play');
        return;
      }

      await safeCleanupPlayer();

      if (Platform.OS === 'ios') {
        await setAudioModeAsync({
          playsInSilentMode: true,
          interruptionMode: 'mixWithOthers',
          allowsRecording: false,
          shouldPlayInBackground: false
        });

        await setIsAudioActiveAsync(true);
      }

      if (!recordPlayerRef.current) {
        recordPlayerRef.current = createAudioPlayer();
      }

      const fileInfo = await FileSystem.getInfoAsync(pathToPlay);
      if (!fileInfo.exists) {
        console.error(`File does not exist: ${pathToPlay}`);
        return;
      }

      recordPlayerRef.current.replace({ uri: pathToPlay });
      recordPlayerRef.current.volume = 1.0;

      recordPlayerRef.current.play();
      setIsPlaying(true);

      recordPlayerRef.current.addListener('playbackStatusUpdate', (status: AudioStatus) => {
        if (status.currentTime !== undefined && status.duration !== undefined) {
          setCurrentPositionSec(status.currentTime * 1000);
          setCurrentDurationSec(status.duration * 1000);
          setPlayTime(formatTime(Math.floor(status.currentTime)));
          setDuration(formatTime(Math.floor(status.duration)));
        }

        if (status.didJustFinish) {
          setIsPlaying(false);

          if (Platform.OS === 'ios') {
            setIsAudioActiveAsync(false).catch(err =>
              console.error('Error deactivating audio session:', err)
            );
          }
        }
      });

      console.log(`Playing audio with expo-audio: ${pathToPlay}`);
    } catch (error) {
      console.error('Error playing audio:', error);
      setIsPlaying(false);

      if (recordPlayerRef.current) {
        try {
          recordPlayerRef.current.removeAllListeners();
        } catch (cleanupError) {
          console.error('Error cleaning up after playback failure:', cleanupError);
        }
      }

      if (Platform.OS === 'ios') {
        try {
          await setIsAudioActiveAsync(false);
        } catch (audioError) {
          console.error('Error deactivating audio session:', audioError);
        }
      }
    }
  }, [path, formatTime, safeCleanupPlayer, isPlaying]);

  const removeRecord = useCallback(async (recordPath: string) => {
    if (!recordPath) {
      console.error('Cannot delete record: No path provided');
      return;
    }
    
    try {
      const fileInfo = await FileSystem.getInfoAsync(recordPath);
      if (!fileInfo.exists) {
        console.warn(`File does not exist: ${recordPath}`);
        return;
      }

      if (path === recordPath) {
        setPath(undefined);
      }

      await FileSystem.deleteAsync(recordPath);
      console.log(`Record deleted: ${recordPath}`);
    } catch (error) {
      console.error('Error deleting record:', error);
      throw error;
    }
  }, [path]);

  const getRecordingsFromDirectory = useCallback(async (): Promise<Recordings[]> => {
    try {
      const dirInfo = await FileSystem.getInfoAsync(RECORDINGS_DIRECTORY);
      if (!dirInfo.exists) {
        console.log(`Recordings directory does not exist: ${RECORDINGS_DIRECTORY}`);
        await ensureDirectoryExists();
        return [];
      }

      const files = await FileSystem.readDirectoryAsync(RECORDINGS_DIRECTORY);

      if (!files || !Array.isArray(files)) {
        console.warn('Failed to read directory or no files found');
        return [];
      }

      const recordingPromises = files
        .filter(file => file && typeof file === 'string' && file.endsWith(RECORDING_EXTENSION))
        .map(async (file) => {
          try {
            const uri = `${RECORDINGS_DIRECTORY}${file}`;
            const fileInfo = await FileSystem.getInfoAsync(uri);

            if (!fileInfo.exists) {
              console.warn(`File does not exist despite being listed: ${uri}`);
              return null;
            }

            return {
              uri: fileInfo.uri,
              name: file,
              ...(fileInfo.size !== undefined && { size: fileInfo.size }),
            };
          } catch (fileError) {
            console.error(`Error processing file ${file}:`, fileError);
            return null;
          }
        });
      
      // Wait for all promises to resolve and filter out nulls
      const recordings = await Promise.all(recordingPromises);
      return recordings.filter((recording): recording is Recordings => recording !== null);
    } catch (error) {
      console.error('Error reading recordings directory:', error);
      return [];
    }
  }, [RECORDINGS_DIRECTORY, RECORDING_EXTENSION, ensureDirectoryExists]);

  const getPlayList = useCallback(async (): Promise<Recordings[]> => {
    try {
      // Make sure the recordings directory exists
      await ensureDirectoryExists();
      
      // Get recordings from the directory
      const recordings = await getRecordingsFromDirectory();
      
      // Sort recordings by name (which includes timestamp) in descending order (newest first)
      return recordings.sort((a, b) => {
        // Extract timestamps from filenames (assuming format recording_TIMESTAMP.m4a)
        const timestampA = a.name.match(/recording_(\d+)/)?.[1];
        const timestampB = b.name.match(/recording_(\d+)/)?.[1];
        
        if (timestampA && timestampB) {
          return parseInt(timestampB) - parseInt(timestampA);
        }
        
        // Fallback to string comparison if timestamps can't be extracted
        return b.name.localeCompare(a.name);
      });
    } catch (error) {
      console.error('Error getting playlist:', error);
      // Return empty array on error to avoid breaking the UI
      return [];
    }
  }, [ensureDirectoryExists, getRecordingsFromDirectory]);

  return {
    recordSecs,
    recordTime,
    currentPositionSec,
    currentDurationSec,
    playTime,
    duration,
    isRecording,
    isPlaying,
    path,
    
    onRecord,
    onStop,
    onPlay,
    removeRecord,
    getPlayList,
    cleanup,
    formatTime
  };
};

export default useRecordProvider;

export default useRecordProvider;