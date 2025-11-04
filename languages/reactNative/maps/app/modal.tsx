import React, { useMemo } from 'react';
import { StyleSheet, FlatList, Pressable, View } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';

interface User {
  id: string;
  name: string;
  role: string;
}

export default function ModalScreen() {
  const { pinId } = useLocalSearchParams<{ pinId?: string }>();

  const users = useMemo<User[]>(
    () => [
      { id: 'u1', name: 'Alice Johnson', role: 'Admin' },
      { id: 'u2', name: 'Bob Smith', role: 'Editor' },
      { id: 'u3', name: 'Carol Davis', role: 'Viewer' },
      { id: 'u4', name: 'David Wilson', role: 'Contributor' },
      { id: 'u5', name: 'Eve Martinez', role: 'Moderator' },
      { id: 'u6', name: 'Frank Brown', role: 'Member' },
      { id: 'u7', name: 'Grace Lee', role: 'Member' },
      { id: 'u8', name: 'Henry Walker', role: 'Member' },
      { id: 'u9', name: 'Ivy Young', role: 'Member' },
      { id: 'u10', name: 'Jack King', role: 'Member' },
    ],
    []
  );

  return (
    <ThemedView style={styles.container}>
      <View style={styles.header}>
        <ThemedText type="title">Users near this pin</ThemedText>
        {pinId ? (
          <ThemedText type="default" style={styles.subtitle}>Pin: {pinId}</ThemedText>
        ) : null}
        <Pressable onPress={() => router.back()} style={styles.closeBtn}>
          <ThemedText style={styles.closeBtnText}>Close</ThemedText>
        </Pressable>
      </View>

      <FlatList
        data={users}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        renderItem={({ item }) => (
          <View style={styles.userRow}>
            <View style={styles.avatar} />
            <View style={{ flex: 1 }}>
              <ThemedText type="defaultSemiBold">{item.name}</ThemedText>
              <ThemedText type="default">{item.role}</ThemedText>
            </View>
          </View>
        )}
      />
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 24,
  },
  header: {
    paddingHorizontal: 16,
    paddingBottom: 12,
  },
  subtitle: {
    marginTop: 4,
    opacity: 0.7,
  },
  closeBtn: {
    alignSelf: 'flex-start',
    marginTop: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
    backgroundColor: '#007aff',
  },
  closeBtnText: {
    color: '#fff',
    fontWeight: '600',
  },
  listContent: {
    paddingHorizontal: 16,
    paddingBottom: 24,
  },
  userRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
  },
  avatar: {
    width: 36,
    height: 36,
    borderRadius: 18,
    marginRight: 12,
    backgroundColor: '#ccc',
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: 'rgba(0,0,0,0.1)',
  },
});
