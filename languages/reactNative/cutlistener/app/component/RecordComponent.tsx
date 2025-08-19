import {TouchableOpacity, SafeAreaView, StyleSheet, Text} from "react-native";
import {FontAwesome} from "@expo/vector-icons";
import {useEffect, useState, useRef} from "react";
import ListComponent from "@/app/component/ListComponent";
import {ListItemProps, PluginType} from "@/app/types/types";
import RecordProvider from "@/app/helper/recordProvider";
import SequentialAudioPlayer from "@/app/helper/sequentialPlayer";

interface RecordComponentProps {
    testID?: string
}

export default function RecordComponent({testID}: RecordComponentProps) {
    const [recordings, setRecordings] = useState<ListItemProps[]>([]);
    const [isRecording, setIsRecording] = useState<boolean>(false);
    
    // Create a RecordProvider instance
    const recordProviderRef = useRef<RecordProvider | null>(null);
    
    // Create a separate provider for playing sounds from assets/sounds folder
    const assetSoundPlayerRef = useRef<SequentialAudioPlayer | null>(null);

    // Initialize RecordProvider and AssetSoundPlayer
    useEffect(() => {
        // Create a new RecordProvider instance
        recordProviderRef.current = new RecordProvider(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER);
        
        // Create a new SequentialAudioPlayer instance for asset sounds
        const assetSounds = ['one.mp3', 'two.mp3', 'three.mp3'];
        assetSoundPlayerRef.current = new SequentialAudioPlayer(assetSounds);
        
        // Cleanup on unmounting
        return () => {
            if (recordProviderRef.current) {
                recordProviderRef.current.cleanup();
            }
            if (assetSoundPlayerRef.current) {
                assetSoundPlayerRef.current.stop();
            }
        };
    }, []);

    useEffect(() => {
        const loadRecordings = async () => {
            try {
                if (!recordProviderRef.current) return;
                const recordingsList = await recordProviderRef.current.getPlayList();
                setRecordings(recordingsList.map((record) => ({
                    id: record.name,
                    title: record.name,
                    recording: record
                })));
            } catch (error) {
                console.error('Error loading recordings:', error);
            }
        };

        loadRecordings();
    }, []);

    const onPressRecord = async () => {
        try {
            if (!recordProviderRef.current || !assetSoundPlayerRef.current) return;
            
            // Start playing asset sounds with 5-second intervals
            assetSoundPlayerRef.current.start();
            
            // Start recording
            await recordProviderRef.current.onRecord();
            setIsRecording(recordProviderRef.current.isRecording);
        } catch (error) {
            console.error('Error starting record:', error);
        }
    };

    const onPressStop = async () => {
        try {
            if (!recordProviderRef.current) return;
            
            // Stop asset sound player
            if (assetSoundPlayerRef.current) {
                assetSoundPlayerRef.current.stop();
            }
            
            // Stop recording
            await recordProviderRef.current.onStop();
            setIsRecording(recordProviderRef.current.isRecording);

            const updatedRecordings = await recordProviderRef.current.getPlayList();
            setRecordings(updatedRecordings.map((record) => ({
                id: record.name,
                title: record.name,
                recording: record
            })));
        } catch (error) {
            console.error('Error stopping record:', error);
        }
    };
    
    const onPressPlay = async (recordPath?: string) => {
        if (!recordProviderRef.current) return;
        await recordProviderRef.current.onPlay(recordPath);
    }

    const onDeleteRecord = async (recordPath: string) => {
        if (!recordProviderRef.current) return;
        await recordProviderRef.current.removeRecord(recordPath);
        const updatedRecordings = await recordProviderRef.current.getPlayList();
        setRecordings(updatedRecordings.map((record) => ({
            id: record.name,
            title: record.name,
            recording: record
        })));
    }


    return (
        <SafeAreaView style={styles.component} testID={testID || "record-component"}>
            <Text style={styles.subHeader}>
                RN Audio Recorder Player
            </Text>
            {isRecording ?
                <TouchableOpacity
                    onPress={onPressStop}
                    style={[styles.iconButton, styles.buttonContent]}
                    testID="stop-button"
                >
                    <Text>Stop</Text>
                    <FontAwesome name="stop" size={24} color="#c10d10"/>
                </TouchableOpacity>
                :
                <TouchableOpacity
                    onPress={onPressRecord}
                    style={[styles.iconButton, styles.buttonContent]}
                    testID="record-button"
                >
                    <Text style={styles.buttonText}>Record 10 cuts</Text>
                    <FontAwesome name="microphone" size={24} color="#1ac10d"/>
                </TouchableOpacity>
            }

            <TouchableOpacity
                onPress={() => onPressPlay()}
                style={[styles.iconButton, styles.buttonContent]}
                testID="play-button"
            >
                <Text style={styles.buttonText}>Play</Text>
                <FontAwesome name="play" size={24} color="#000"/>
            </TouchableOpacity>
            <ListComponent
                data={recordings}
                onItemPress={(item) => onPressPlay(item.recording.uri)}
                onDelete={(item) => onDeleteRecord(item.recording.uri)}
            />
        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    subHeader: {
        fontSize: 18,
        fontFamily: "Roboto",
        marginBottom: 10,
    },
    component: {
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#dceaf9",
        borderColor: "#97a3b4",
        borderWidth: 2,
        borderRadius: 10,
        width: "100%",
        padding: 10,
    },
    iconButton: {
        padding: 10,
        marginVertical: 5,
        borderRadius: 25,
        backgroundColor: '#fff',
        shadowColor: '#000',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
        width: '80%',
    },
    buttonContent: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    buttonText: {
        fontSize: 16,
        fontFamily: 'Roboto',
        color: '#666',
        marginRight: 10,
    },
});