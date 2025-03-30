import { Text, View, StyleSheet } from "react-native";
import { Link } from 'expo-router';

export default function Index() {
    return (
        <View
            style={styles.container}
        >
            <Text style={styles.text}>Home screen.</Text>
            <Link href="//tabs/aboutabout" style={styles.link}>To the about screen</Link>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#25292e',
        alignItems: 'center',
        justifyContent: 'center',
    },
    text: {
        fontSize: 20,
    },
    link: {
        fontSize: 20,
        textDecorationLine: 'underline',
        color: 'white',
    }
})
