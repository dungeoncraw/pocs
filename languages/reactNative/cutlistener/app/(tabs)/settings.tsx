import {StyleSheet, Text, View} from "react-native";
import Separator from "@/app/component/Separator";
import PluginSelect from "@/app/component/PluginSelect";
import { PluginType } from '@/app/types/types';
import {pluginSelectAtom} from "@/app/state/PluginSelectAtom";
import { useSetAtom } from 'jotai'

export const AVAILABLE_PLUGINS = [
    {
        id: PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER,
        label: "React Native Audio Recorder Player",
    },
    {
        id: PluginType.EXPO_AUDIO,
        label: "Expo Audio",
    },
];
export default function Settings() {
    const setPlugin = useSetAtom(pluginSelectAtom)

    return (
        <View
            style={styles.view}
        >
            <Text style={styles.main}>Available plugins</Text>
            <Separator testID="separator"/>
            <PluginSelect data={AVAILABLE_PLUGINS} onSelect={(item) => setPlugin(item.id)}/>
        </View>
    );
}

const styles = StyleSheet.create({
    main: {
        fontSize: 25,
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
