import {TouchableOpacity, SafeAreaView, StyleSheet, Text} from "react-native";
import RecordProvider from "@/app/helper/recordProvider";
import {pluginSelectAtom} from "@/app/state/PluginSelectAtom";
import {useAtomValue} from 'jotai'
import {AVAILABLE_PLUGINS} from "@/app/(tabs)/settings";
import {FontAwesome} from "@expo/vector-icons";
import {useEffect, useRef, useState} from "react";
import ListComponent from "@/app/component/ListComponent";
import {ListItemProps, Recordings} from "@/app/types/types";

interface RecordComponentProps {
    testID?: string
}

export default function RecordComponent({testID}: RecordComponentProps) {
    const pluginType = useAtomValue(pluginSelectAtom);
    const [isRecording, setIsRecording] = useState(false);
    const [recordings, setRecordings] = useState<ListItemProps[]>([]);
    const audioRecordPlayerRef = useRef<RecordProvider | null>(null);

    useEffect(() => {
        audioRecordPlayerRef.current = new RecordProvider(pluginType);
        audioRecordPlayerRef.current.getPlayList().then((r: Recordings[]) => {
            setRecordings(r.map((record) => ({
                id: record.name,
                title: record.name,
                recording: record
            })))
        });
        return () => {
            if (audioRecordPlayerRef.current) {
                // Call cleanup to properly release all audio resources
                audioRecordPlayerRef.current.cleanup().catch(err => 
                    console.error('Error during cleanup:', err)
                );
            }
        };
    }, [pluginType]);

    const onPressRecord = async () => {
        if (!audioRecordPlayerRef.current) return;
        setIsRecording(true);
        await audioRecordPlayerRef.current.onRecord();
    };

    const onPressStop = async () => {
        if (!audioRecordPlayerRef.current) return;
        setIsRecording(false);
        await audioRecordPlayerRef.current.onStop();
    }
    const onPressPlay = async () => {
        if (!audioRecordPlayerRef.current) return;
        await audioRecordPlayerRef.current.onPlay();
    }

    return (
        <SafeAreaView style={styles.component} testID={testID || "record-component"}>
            <Text style={styles.subHeader}>
                {AVAILABLE_PLUGINS.find((t) => t.id === pluginType)?.label}
            </Text>
            {isRecording ?
                <TouchableOpacity
                    onPress={onPressStop}
                    style={styles.iconButton}
                    testID="stop-button"
                >
                    <FontAwesome name="stop" size={24} color="#c10d10"/>
                </TouchableOpacity>
                :
                <TouchableOpacity
                    onPress={onPressRecord}
                    style={styles.iconButton}
                    testID="record-button"
                >
                    <FontAwesome name="microphone" size={24} color="#1ac10d"/>
                </TouchableOpacity>
            }

            <TouchableOpacity
                onPress={onPressPlay}
                style={styles.iconButton}
                testID="play-button"
            >
                <FontAwesome name="play" size={24} color="#000"/>
            </TouchableOpacity>
            <ListComponent data={recordings} onItemPress={() => {}}/>
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
    },
});
