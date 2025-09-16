import {Pressable, StyleSheet, View, Text} from 'react-native';
import {useSpring, animated} from "@react-spring/native";
import {useSpringRef} from "@react-spring/core";

export default function HomeScreen() {
    const colorApi = useSpringRef();
    const colorSpring = useSpring({
        ref: colorApi,
        from: {color: 'rgba(235, 64, 52, 1)'},
    });

    const scaleApi = useSpringRef();
    const scaleSpring = useSpring({
        ref: scaleApi,
        from: {scale: 1},
    });

    const moveApi = useSpringRef();
    const moveSpring = useSpring({
        ref: moveApi,
        from: {translateX: 0},
    });

    const handleColorClick = () => {
        colorApi.start({
            to: {
                color: colorSpring.color.get() === 'rgba(235, 64, 52, 1)' 
                    ? 'rgba(52, 104, 235, 1)' 
                    : 'rgba(235, 64, 52, 1)'
            },
        });
    };

    const handleScaleClick = () => {
        scaleApi.start({
            to: {
                scale: scaleSpring.scale.get() === 1 ? 1.5 : 1
            },
            config: { tension: 300, friction: 10 }
        });
    };

    const handleMoveClick = () => {
        moveApi.start({
            to: {
                translateX: moveSpring.translateX.get() === 0 ? 50 : 0
            },
            config: { tension: 200, friction: 25 }
        });
    };

    return (
        <View style={styles.container}>
            <View style={styles.buttonContainer}>
                <Pressable onPress={handleColorClick} style={styles.button}>
                    <animated.Text style={[styles.buttonText, colorSpring]}>
                        Color animation
                    </animated.Text>
                </Pressable>

                <Pressable onPress={handleScaleClick} style={styles.button}>
                    <animated.View style={[styles.animatedButton, {
                        transform: [{scale: scaleSpring.scale}]
                    }]}>
                        <Text style={styles.buttonText}>Scale animation</Text>
                    </animated.View>
                </Pressable>

                <animated.View style={{
                    transform: [{translateX: moveSpring.translateX}]
                }}>
                    <Pressable onPress={handleMoveClick} style={styles.button}>
                        <Text style={styles.buttonText}>Move animation</Text>
                    </Pressable>
                </animated.View>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f5f5f5',
    },
    buttonContainer: {
        gap: 30,
        alignItems: 'center',
    },
    button: {
        padding: 15,
        backgroundColor: '#fff',
        borderRadius: 10,
        shadowColor: '#000',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
    },
    animatedButton: {
        padding: 0,
    },
    buttonText: {
        fontSize: 16,
        fontWeight: '600',
        textAlign: 'center',
        color: '#333',
    },
});
