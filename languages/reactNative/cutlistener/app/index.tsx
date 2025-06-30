import { Text, View } from "react-native";
import {useEffect} from "react";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import RecordComponent, {PluginType} from "@/app/component/RecordComponent";


export default function Index() {
    useEffect(() => {
        requestMicrophonePermission().then(() => console.log("permission granted"));
    }, []);

  return (
    <View
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Text>Testing recording audio plugins</Text>
        <RecordComponent text={"RN Audio Recorder Player"} pluginType={PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER} />
    </View>
  );
}
