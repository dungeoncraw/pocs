import {StyleSheet, View} from 'react-native';

import {useSpring, animated} from "@react-spring/native";

export default function HomeScreen() {
  const springs = useSpring({
      from: { color: 'red' },
      to: { color: 'blue' },
      config: { duration: 1000 },
  });
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
