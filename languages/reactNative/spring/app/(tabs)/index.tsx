import {StyleSheet, View} from 'react-native';

import {useSpring, animated} from "@react-spring/native";
// https://medium.com/@robinviktorsson/understanding-symbols-in-typescript-a-deep-dive-with-practical-examples-82011a838783
class Person {
    constructor(public name: string) {}
    get[Symbol.toStringTag]() {
        return `${this.name} is a person.`;
    }
}

export default function HomeScreen() {
  const springs = useSpring({
      from: { color: 'red' },
      to: { color: 'blue' },
      config: { duration: 1000 },
  });
  const person = new Person('John');
  // This will print "[object Object is a person.]" using Symbol.toStringTag from TS
  // pretty much is for debugging purposes
  console.log(Object.prototype.toString.call(person));

  return (
      <View style={styles.container}>
          <animated.Text
              style={{
                  ...springs,
              }}
          > This is an animation</animated.Text>
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
