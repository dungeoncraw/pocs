import {StyleSheet, Text, View} from "react-native";
import {useEffect} from "react";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import RecordComponent, {PluginType} from "@/app/component/RecordComponent";
import Separator from "@/app/component/Separator";


export default function Index() {
    useEffect(() => {
        requestMicrophonePermission().then(() => console.log("permission granted"));
    }, []);

  return (
    <View
      style={styles.view}
    >
        <Text style={styles.main}>Testing recording audio plugins</Text>
        <Separator />
        <RecordComponent text={"RN Audio Recorder Player"} pluginType={PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER} />
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
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f9f6ea",
        borderColor: "#dceaf9",
        borderWidth: 2,
        borderRadius: 10,
    },
});