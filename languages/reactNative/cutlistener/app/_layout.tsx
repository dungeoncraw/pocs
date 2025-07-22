import { Stack } from "expo-router";
import { Provider } from 'jotai'
import { StyleSheet, View } from 'react-native';

export default function RootLayout() {
  return (
    <Provider>
      <View style={styles.container}>
        <Stack screenOptions={{ 
          headerShown: true,
          contentStyle: { flex: 1 }
        }}>
          <Stack.Screen 
            name="(tabs)" 
            options={{ 
              headerShown: true, 
              title: 'Cutlistener',
              headerStyle: {
                backgroundColor: '#14161a',
              },
              headerTintColor: '#dceaf9',
            }} 
          />
        </Stack>
      </View>
    </Provider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#14161a',
  },
});