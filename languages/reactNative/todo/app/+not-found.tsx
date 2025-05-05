import { View, StyleSheet } from 'react-native';
import { Link, Stack } from 'expo-router';

export default function NotFoundScreen () {
    return(
        <>
            <Stack.Screen options={{ title: 'Oops!' }} />
            <View style={styles.container} >
                <Link href="/app/(tabs)" style={styles.button}>
                    Go to home screen!
                </Link>
            </View>
        </>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#25292e',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
    },
    button: {
        fontSize: 20,
        textDecorationLine: 'underline',
        color: 'white',
    }
})