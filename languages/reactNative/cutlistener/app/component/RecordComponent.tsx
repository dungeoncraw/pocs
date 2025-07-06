import {Button, Text, SafeAreaView, Platform, StyleSheet} from "react-native";
import RecordProvider from "@/app/helper/recordProvider";

export enum PluginType {
    REACT_NATIVE_AUDIO_RECORDER_PLAYER = "RECORDER_PLAYER",
    EXPO_AUDIO = "EXPO_AUDIO",
}

type IProps = {
    text: string;
    pluginType: PluginType;
}

export default function RecordComponent({text, pluginType}: IProps) {
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