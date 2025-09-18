import React, { useState, useEffect } from 'react';
import { View, StyleSheet, Dimensions, Text } from 'react-native';
import { Accelerometer } from 'expo-sensors';
import Animated, { 
  useSharedValue, 
  useAnimatedStyle, 
  withSpring,
  interpolate,
  Extrapolate
} from 'react-native-reanimated';
import { ThemedView } from '@/components/themed-view';
import { ThemedText } from '@/components/themed-text';

const { width: screenWidth, height: screenHeight } = Dimensions.get('window');
const CIRCLE_SIZE = 60;

export default function HomeScreen() {
  const [sensorData, setSensorData] = useState({ x: 0, y: 0, z: 0 });
  const [isActive, setIsActive] = useState(true);

  const circleX = useSharedValue(screenWidth / 2 - CIRCLE_SIZE / 2);
  const circleY = useSharedValue(screenHeight / 2 - CIRCLE_SIZE / 2);

  useEffect(() => {
    const subscription = Accelerometer.addListener(accelerometerData => {
      setSensorData(accelerometerData);

      if (isActive) {
        // Inverter X para que a inclinação para direita mova o círculo para direita
        const newX = interpolate(
          -accelerometerData.x,
          [-0.5, 0.5],
          [50, screenWidth - CIRCLE_SIZE - 50],
          Extrapolate.CLAMP
        );

        // Usar Y para movimento vertical (opcional)
        const newY = interpolate(
          accelerometerData.y,
          [-0.5, 0.5],
          [100, screenHeight - CIRCLE_SIZE - 200],
          Extrapolate.CLAMP
        );

        circleX.value = withSpring(newX, { damping: 15, stiffness: 150 });
        circleY.value = withSpring(newY, { damping: 15, stiffness: 150 });
      }
    });

    // Definir intervalo de atualização para 100ms
    Accelerometer.setUpdateInterval(100);

    return () => {
      subscription.remove();
    };
  }, [isActive]);

  const animatedStyle = useAnimatedStyle(() => {
    return {
      transform: [
        { translateX: circleX.value },
        { translateY: circleY.value },
      ],
    };
  });

  const toggleSensor = () => {
    setIsActive(!isActive);
  };

  return (
    <ThemedView style={styles.container}>
      <ThemedView style={styles.header}>
        <ThemedText type="title">Sensor Circle</ThemedText>
        <ThemedText style={styles.instruction}>
          Incline seu dispositivo para mover o círculo!
        </ThemedText>
      </ThemedView>

      <View style={styles.gameArea}>
        <Animated.View style={[styles.circle, animatedStyle]} />
      </View>

      <ThemedView style={styles.sensorInfo}>
        <ThemedText type="subtitle">Dados do Acelerômetro:</ThemedText>
        <Text style={styles.sensorText}>X: {sensorData.x.toFixed(3)}</Text>
        <Text style={styles.sensorText}>Y: {sensorData.y.toFixed(3)}</Text>
        <Text style={styles.sensorText}>Z: {sensorData.z.toFixed(3)}</Text>

        <ThemedText 
          style={[styles.toggleButton, { backgroundColor: isActive ? '#007AFF' : '#666' }]}
          onPress={toggleSensor}
        >
          {isActive ? 'Pausar Sensor' : 'Ativar Sensor'}
        </ThemedText>
      </ThemedView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 60,
  },
  header: {
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  instruction: {
    textAlign: 'center',
    marginTop: 10,
    fontSize: 16,
    opacity: 0.8,
  },
  gameArea: {
    flex: 1,
    position: 'relative',
    backgroundColor: 'transparent',
  },
  circle: {
    position: 'absolute',
    width: CIRCLE_SIZE,
    height: CIRCLE_SIZE,
    borderRadius: CIRCLE_SIZE / 2,
    backgroundColor: '#FF6B6B',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  sensorInfo: {
    padding: 20,
    alignItems: 'center',
    borderTopWidth: 1,
    borderTopColor: 'rgba(255, 255, 255, 0.1)',
  },
  sensorText: {
    fontSize: 14,
    marginVertical: 2,
    fontFamily: 'monospace',
    color: '#666',
  },
  toggleButton: {
    marginTop: 15,
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 25,
    color: 'white',
    textAlign: 'center',
    fontWeight: 'bold',
    minWidth: 150,
  },
});
