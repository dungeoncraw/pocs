import React, { useMemo, useState } from 'react';
import { StyleSheet, View, Pressable } from 'react-native';
import MapLibreGL from '@maplibre/maplibre-react-native';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';

interface Pin {
  id: string;
  coordinate: { latitude: number; longitude: number };
  lngLat: [number, number];
  title?: string;
}

export default function MapScreen() {
  const colorScheme = useColorScheme();
  const [pins, setPins] = useState<Pin[]>([]);

  const initialCenter = useMemo<[number, number]>(() => [0, 0], []);

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

  const removePin = (id: string) => {
    setPins((prev) => prev.filter((p) => p.id !== id));
  };

  const clearAll = () => setPins([]);

  const tint = Colors[colorScheme ?? 'light'].tint;

  // Using a public MapLibre style URL (no tokens required)
  return (
    <ThemedView style={styles.container}>
      <MapLibreGL.MapView
        style={StyleSheet.absoluteFill}
        styleURL="https://demotiles.maplibre.org/style.json"
        onPress={onMapPress}
      >
        <MapLibreGL.Camera centerCoordinate={initialCenter} zoomLevel={2} />

        {pins.map((pin) => (
          <MapLibreGL.PointAnnotation
            key={pin.id}
            id={pin.id}
            coordinate={pin.lngLat}
            title={pin.title}
            onSelected={() => removePin(pin.id)}
          >
            {/* Default view is fine; could customize the marker here */}
          </MapLibreGL.PointAnnotation>
        ))}
      </MapLibreGL.MapView>

      {/* Floating controls */}
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
});
