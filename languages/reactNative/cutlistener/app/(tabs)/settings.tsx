import {StyleSheet, Text, View} from "react-native";
export default function Settings() {

    return (
        <View
            style={styles.view}
        >
            <Text style={styles.main}>Settings for audio plugin</Text>
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