import {StyleSheet, Text, View} from "react-native";
import {useEffect, useState} from "react";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import RecordComponent, {PluginType} from "@/app/component/RecordComponent";
import Separator from "@/app/component/Separator";
import PluginSelect from "@/app/component/PluginSelect";

const AVAILABLE_PLUGINS = [
    {
        id: PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER,
        label: "React Native Audio Recorder Player",
    },
];

export default function Index() {
    const [selectedPlugin, setSelectedPlugin] = useState<PluginType>(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER);
    useEffect(() => {
        requestMicrophonePermission().then(() => console.log("permission granted"));
    }, []);

    return (
        <View
            style={styles.view}
        >
            <Text style={styles.main}>Testing recording audio plugins</Text>
            <Separator/>
            <PluginSelect data={AVAILABLE_PLUGINS} onSelect={(item) => setSelectedPlugin(item.id)}/>
            <RecordComponent text={"RN Audio Recorder Player"}
                             pluginType={selectedPlugin}/>
        </View>
    );
}

const styles = StyleSheet.create({
    main: {
        fontSize: 25,
        fontFamily: "Roboto",
    },
    select: {
        fontSize: 18,
        fontFamily: "Roboto",
    },
    view: {
        flex: 1,
        justifyContent: "flex-start",
        alignItems: "center",
        backgroundColor: "#f9f6ea",
        borderColor: "#dceaf9",
        borderWidth: 2,
        borderRadius: 10,
        paddingTop: 10,
    },
});