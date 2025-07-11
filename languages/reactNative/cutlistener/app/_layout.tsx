import { Stack } from "expo-router";
import { Provider } from 'jotai'

export default function RootLayout() {
  return (
    <Provider>
      <Stack>
        <Stack.Screen name="(tabs)" options={{ headerShown: true, title: 'Cutlistener' }} />
      </Stack>
    </Provider>
  );
}
