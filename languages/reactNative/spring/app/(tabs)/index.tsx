import {Pressable, StyleSheet, View, Text, Dimensions} from 'react-native';
import {useSpring, animated} from "@react-spring/native";
import {useSpringRef} from "@react-spring/core";

const {width: screenWidth, height: screenHeight} = Dimensions.get('window');

export default function HomeScreen() {
    const moveApi = useSpringRef();
    const moveSpring = useSpring({
        ref: moveApi,
        from: {translateX: 0, translateY: 0},
    });

    const generateRandomPosition = () => {
        const buttonWidth = 150;
        const buttonHeight = 50;
        const margin = 20;

        const maxX = screenWidth - buttonWidth - margin;
        const maxY = screenHeight - buttonHeight - margin - 120;

        const randomX = Math.random() * maxX - (screenWidth / 2 - buttonWidth / 2);
        const randomY = Math.random() * maxY - (screenHeight / 2 - buttonHeight / 2);

        return {x: randomX, y: randomY};
    };

    const handleMoveClick = () => {
        const newPosition = generateRandomPosition();
        moveApi.start({
            to: {
                translateX: newPosition.x,
                translateY: newPosition.y
            },
            // lower tension, lower speed on animation, 
            // less friction, more bounce effect
            config: { tension: 100, friction: 5 }
        });
    };

    return (
        <View style={styles.container}>
            <animated.View style={{
                transform: [
                    {translateX: moveSpring.translateX},
                    {translateY: moveSpring.translateY}
                ]
            }}>
                <Pressable onPress={handleMoveClick} style={styles.button}>
                    <Text style={styles.buttonText}>Toque para mover!</Text>
                </Pressable>
            </animated.View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f5f5f5',
    },
    button: {
        padding: 15,
        paddingHorizontal: 25,
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
        minWidth: 150,
    },
    buttonText: {
        fontSize: 16,
        fontWeight: '600',
        textAlign: 'center',
        color: '#333',
    },
});
