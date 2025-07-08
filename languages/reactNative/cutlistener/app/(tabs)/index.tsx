import {StyleSheet, Text, View} from "react-native";
import {useEffect} from "react";
import {requestMicrophonePermission} from "@/app/helper/requestPermission";
import RecordComponent from "@/app/component/RecordComponent";
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
            <Separator testID="separator"/>
            <RecordComponent testID="record-component"/>
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
