import {TouchableOpacity, SafeAreaView, StyleSheet, Text} from "react-native";
import {FontAwesome} from "@expo/vector-icons";
import {useEffect, useState} from "react";
import ListComponent from "@/app/component/ListComponent";
import {ListItemProps} from "@/app/types/types";
import useRecordProvider from "@/app/hooks/useRecordProvider";

interface RecordComponentProps {
    testID?: string
}

export default function RecordComponent({testID}: RecordComponentProps) {
    const [recordings, setRecordings] = useState<ListItemProps[]>([]);
    
    // Use the new hook instead of class-based RecordProvider
    const {
        isRecording,
        onRecord,
        onStop,
        onPlay,
        removeRecord,
        getPlayList,
    } = useRecordProvider();

    useEffect(() => {
        const loadRecordings = async () => {
            try {
                const recordingsList = await getPlayList();
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
    }, [getPlayList]);

    const onPressRecord = async () => {
        try {
            await onRecord();
        } catch (error) {
            console.error('Error starting record:', error);
        }
    };

    const onPressStop = async () => {
        try {
            await onStop();

            const updatedRecordings = await getPlayList();
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
        await onPlay(recordPath);
    }

    const onDeleteRecord = async (recordPath: string) => {
        await removeRecord(recordPath);
        const updatedRecordings = await getPlayList();
        setRecordings(updatedRecordings.map((record) => ({
            id: record.name,
            title: record.name,
            recording: record
        })));
    }

    return (
        <SafeAreaView style={styles.component} testID={testID || "record-component"}>
            <Text style={styles.subHeader}>
                Expo Audio Recorder
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
                    <Text style={styles.buttonText}>Record</Text>
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