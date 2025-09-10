import {Pressable, StyleSheet, View} from 'react-native';

import {useSpring, animated} from "@react-spring/native";
import {useSpringRef} from "@react-spring/core";

// https://medium.com/@robinviktorsson/understanding-symbols-in-typescript-a-deep-dive-with-practical-examples-82011a838783
class Person {
    constructor(public name: string) {
    }

    get [Symbol.toStringTag]() {
        return `${this.name} is a person.`;
    }
}

export default function HomeScreen() {
    const api = useSpringRef()
    const springs = useSpring({
        ref: api,
        //   this is funny, react spring only works with rgba colors
        from: {color: 'rgba(235, 64, 52, 1)'},
    });
    const handleClick = () => {
        console.log('springs', springs.color.get())
        api.start({
            to: {color: springs.color.get() === 'rgba(235, 64, 52, 1)' ? 'rgba(52, 104, 235, 1)' : 'rgba(235, 64, 52, 1)'},
        })
    }
    const person = new Person('John');
    // This will print "[object Object is a person.]" using Symbol.toStringTag from TS
    // pretty much is for debugging purposes
    console.log(Object.prototype.toString.call(person));

    return (
        <View style={styles.container}>
            <Pressable onPress={handleClick}>
                <animated.Text
                    style={{
                        ...springs,
                    }}
                > This is an animation and don&apos;t rerender {Math.random()}</animated.Text>
            </Pressable>
        </View>

    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
    },
    text: {
        gap: 8,
        marginBottom: 8,
    },
});
