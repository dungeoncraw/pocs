import {Button, Text, SafeAreaView, Platform, StyleSheet} from "react-native";
import RecordProvider from "@/app/helper/recordProvider";
import {pluginSelectAtom} from "@/app/state/PluginSelectAtom";
import { useAtomValue } from 'jotai'
import {AVAILABLE_PLUGINS} from "@/app/(tabs)/settings";

export default function RecordComponent() {
    const pluginType = useAtomValue(pluginSelectAtom);

    const path = Platform.select({
        ios: undefined,
        android: undefined
    });
    const audioRecordPLayer = new RecordProvider(pluginType, path);

    const onPressRecord = async () => {
        await audioRecordPLayer.onRecord();
    };

    const onPressStop = async () => {
        await audioRecordPLayer.onStop();
    }
    const onPressPlay = async () => {
        await audioRecordPLayer.onPlay();
    }
    return (
        <SafeAreaView style={styles.component}>
            <Text style={styles.subHeader}>{AVAILABLE_PLUGINS.find((t) => t.id === pluginType)?.label}</Text>
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