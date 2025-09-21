import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Alert } from 'react-native';
import { Magnetometer } from 'expo-sensors';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';

interface MagnetometerData {
  x: number;
  y: number;
  z: number;
}

export default function CompassScreen() {
  const colorScheme = useColorScheme();
  const [magnetData, setMagnetData] = useState<MagnetometerData>({ x: 0, y: 0, z: 0 });
  const [isAvailable, setIsAvailable] = useState<boolean>(false);
  const [subscription, setSubscription] = useState<any>(null);
  const [heading, setHeading] = useState<number>(0);

  useEffect(() => {
    checkAvailability();
    return () => {
      unsubscribe();
    };
  }, []);

  useEffect(() => {
    const angle = Math.atan2(magnetData.y, magnetData.x) * (180 / Math.PI);
    const normalizedAngle = angle < 0 ? angle + 360 : angle;
    setHeading(Math.round(normalizedAngle));
  }, [magnetData]);

  const checkAvailability = async () => {
    const available = await Magnetometer.isAvailableAsync();
    setIsAvailable(available);
    if (!available) {
      Alert.alert('Error', 'The magnetometer not available');
    }
  };

  const subscribe = () => {
    if (!isAvailable) {
      Alert.alert('Error', 'The magnetometer not available');
      return;
    }

    setSubscription(
      Magnetometer.addListener(magnetometerData => {
        setMagnetData(magnetometerData);
      })
    );
    // update interval in milliseconds
    Magnetometer.setUpdateInterval(100);
  };

  const unsubscribe = () => {
    subscription && subscription.remove();
    setSubscription(null);
  };

  const getDirection = (heading: number): string => {
    if (heading >= 337.5 || heading < 22.5) return 'N';
    if (heading >= 67.5 && heading < 112.5) return 'E';
    if (heading >= 157.5 && heading < 202.5) return 'S';
    if (heading >= 247.5 && heading < 292.5) return 'O';
    return 'N';
  };

  const getDirectionName = (heading: number): string => {
    if (heading >= 337.5 || heading < 22.5) return 'North';
    if (heading >= 67.5 && heading < 112.5) return 'East';
    if (heading >= 157.5 && heading < 202.5) return 'South';
    if (heading >= 247.5 && heading < 292.5) return 'West';
    return 'North';
  };
  console.log(magnetData)
  return (
    <ThemedView style={styles.container}>
      <ThemedText type="title" style={styles.title}>
        Compass
      </ThemedText>

      {isAvailable ? (
        <>
          <View style={styles.compassContainer}>
            <View style={[styles.compass, { borderColor: Colors[colorScheme ?? 'light'].text }]}>
              <View 
                style={[
                  styles.needle, 
                  { 
                    backgroundColor: Colors[colorScheme ?? 'light'].tint,
                    transform: [{ rotate: `${heading}deg` }] 
                  }
                ]} 
              />
              <Text style={[styles.northMark, { color: Colors[colorScheme ?? 'light'].text }]}>N</Text>
              <Text style={[styles.eastMark, { color: Colors[colorScheme ?? 'light'].text }]}>E</Text>
              <Text style={[styles.southMark, { color: Colors[colorScheme ?? 'light'].text }]}>S</Text>
              <Text style={[styles.westMark, { color: Colors[colorScheme ?? 'light'].text }]}>W</Text>
            </View>
          </View>

          <View style={styles.infoContainer}>
            <ThemedText type="subtitle" style={styles.headingText}>
              {heading}°
            </ThemedText>
            <ThemedText type="default" style={styles.directionText}>
              {getDirection(heading)} - {getDirectionName(heading)}
            </ThemedText>
          </View>

          <View style={styles.controls}>
            <TouchableOpacity
              style={[styles.button, { backgroundColor: Colors[colorScheme ?? 'light'].tint }]}
              onPress={subscription ? unsubscribe : subscribe}
            >
              <Text style={styles.buttonText}>
                {subscription ? 'Stop' : 'Start'}
              </Text>
            </TouchableOpacity>
          </View>
        </>
      ) : (
        <View style={styles.unavailableContainer}>
          <ThemedText type="subtitle" style={styles.unavailableText}>
            Magnetometer Not Available
          </ThemedText>
          <ThemedText style={styles.unavailableDescription}>
            This device does not have a magnetometer or it is not working properly.
          </ThemedText>
        </View>
      )}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    alignItems: 'center',
  },
  title: {
    marginBottom: 30,
    textAlign: 'center',
  },
  compassContainer: {
    alignItems: 'center',
    marginBottom: 30,
  },
  compass: {
    width: 200,
    height: 200,
    borderRadius: 100,
    borderWidth: 3,
    position: 'relative',
    alignItems: 'center',
    justifyContent: 'center',
  },
  needle: {
    width: 4,
    height: 80,
    position: 'absolute',
    top: 10,
    borderRadius: 2,
  },
  northMark: {
    position: 'absolute',
    top: 5,
    fontSize: 18,
    fontWeight: 'bold',
  },
  eastMark: {
    position: 'absolute',
    right: 5,
    fontSize: 18,
    fontWeight: 'bold',
  },
  southMark: {
    position: 'absolute',
    bottom: 5,
    fontSize: 18,
    fontWeight: 'bold',
  },
  westMark: {
    position: 'absolute',
    left: 5,
    fontSize: 18,
    fontWeight: 'bold',
  },
  infoContainer: {
    alignItems: 'center',
    marginBottom: 30,
  },
  headingText: {
    fontSize: 36,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  directionText: {
    fontSize: 18,
    marginBottom: 10,
  },
  dataContainer: {
    alignItems: 'center',
    marginBottom: 30,
    paddingHorizontal: 20,
  },
  dataText: {
    fontSize: 16,
    marginVertical: 2,
    fontFamily: 'monospace',
  },
  controls: {
    alignItems: 'center',
  },
  button: {
    paddingHorizontal: 30,
    paddingVertical: 12,
    borderRadius: 8,
    elevation: 3,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
  unavailableContainer: {
    alignItems: 'center',
    marginTop: 50,
    paddingHorizontal: 20,
  },
  unavailableText: {
    textAlign: 'center',
    marginBottom: 20,
  },
  unavailableDescription: {
    textAlign: 'center',
    fontSize: 16,
    opacity: 0.7,
  },
});
