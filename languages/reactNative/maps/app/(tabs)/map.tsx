import React, { useEffect, useRef, useState } from 'react';
import { StyleSheet, View, Pressable } from 'react-native';
import { MapView, Camera, PointAnnotation } from '@maplibre/maplibre-react-native';
import * as Location from 'expo-location';
import { router } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';

interface Pin {
  id: string;
  coordinate: { latitude: number; longitude: number };
  lngLat: [number, number];
  title?: string;
  isUser?: boolean;
}

export default function MapScreen() {
  const colorScheme = useColorScheme();
  const [pins, setPins] = useState<Pin[]>([]);
  const [center, setCenter] = useState<[number, number]>([0, 0]);
  const [hasUserLocation, setHasUserLocation] = useState(false);
  const [hasCenter, setHasCenter] = useState(false);
  const [mapVersion, setMapVersion] = useState(0);
  const userPinAddedRef = useRef(false);

  useEffect(() => {
    let isMounted = true;
    (async () => {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();
        if (status !== 'granted') {
          throw new Error('location-permission-denied');
        }
        const pos = await Location.getCurrentPositionAsync({});
        const lngLat: [number, number] = [pos.coords.longitude, pos.coords.latitude];
        if (isMounted) {
          setCenter(lngLat);
          setHasUserLocation(true);
          setHasCenter(true);
          if (!userPinAddedRef.current) {
            const [lng, lat] = lngLat;
            const userPin: Pin = {
              id: 'user-location',
              coordinate: { latitude: lat, longitude: lng },
              lngLat,
              title: 'You are here',
              isUser: true,
            };
            setPins((prev) => [userPin, ...prev]);
            userPinAddedRef.current = true;
          }
        }
      } catch (e) {
        const SAO_PAULO: [number, number] = [-46.6333, -23.5505];
        if (isMounted) {
          setCenter(SAO_PAULO);
          setHasCenter(true);
          setHasUserLocation(false);
        }
      }
    })();
    return () => {
      isMounted = false;
    };
  }, []);

  const onMapPress = (e: any) => {
    const coords = e?.geometry?.coordinates as [number, number] | undefined; // [lng, lat]
    if (!coords) return;
    const [lng, lat] = coords;
    const coordinate = { latitude: lat, longitude: lng };
    setPins((prev) => [
      ...prev,
      {
        id: `${lat.toFixed(6)},${lng.toFixed(6)}@${Date.now()}`,
        coordinate,
        lngLat: [lng, lat],
        title: 'Dropped Pin',
      },
    ]);
  };

  const clearAll = () => {
    setPins((prev) => {
      const user = prev.find((p) => p.isUser);
      if (user) return [user];
      if (hasUserLocation) {
        const [lng, lat] = center;
        return [
          {
            id: 'user-location',
            coordinate: { latitude: lat, longitude: lng },
            lngLat: center,
            title: 'You are here',
            isUser: true,
          },
        ];
      }
      return [];
    });
    setMapVersion((v) => v + 1);
  };

  const tint = Colors[colorScheme ?? 'light'].tint;

  // Using a public MapLibre style URL (no tokens required)
  return (
    <ThemedView style={styles.container}>
      <MapView
        key={mapVersion}
        style={StyleSheet.absoluteFill}
        onPress={onMapPress}
      >
        <Camera centerCoordinate={center} zoomLevel={hasUserLocation ? 14 : hasCenter ? 12 : 2} />

        {pins.map((pin) => (
          <PointAnnotation
            key={pin.id}
            id={pin.id}
            coordinate={pin.lngLat}
            title={pin.title}
            onSelected={() => router.push({ pathname: '/modal', params: { pinId: pin.id } })}
          >
            <View style={[styles.marker, pin.isUser ? styles.userMarker : styles.pinMarker]} />
          </PointAnnotation>
        ))}
      </MapView>

      <View style={styles.fabContainer} pointerEvents="box-none">
        <Pressable onPress={clearAll} style={[styles.fab, { backgroundColor: tint }]}>
          <ThemedText style={styles.fabText}>Clear pins</ThemedText>
        </Pressable>
        <View style={styles.hint}>
          <ThemedText type="default">Tap the map to drop a pin.</ThemedText>
          <ThemedText type="default">Tap a marker to remove it.</ThemedText>
        </View>
      </View>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  center: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  fabContainer: {
    position: 'absolute',
    top: 16,
    right: 16,
    left: 16,
    gap: 8,
  },
  fab: {
    alignSelf: 'flex-end',
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 24,
    elevation: 2,
  },
  fabText: {
    color: '#fff',
    fontWeight: '600',
  },
  hint: {
    alignSelf: 'flex-start',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 12,
    backgroundColor: 'rgba(0,0,0,0.35)',
  },
  marker: {
    width: 20,
    height: 20,
    borderRadius: 10,
    borderWidth: 2,
    borderColor: '#fff',
  },
  pinMarker: {
    backgroundColor: '#ff3b30',
  },
  userMarker: {
    backgroundColor: '#34c759',
  },
});
