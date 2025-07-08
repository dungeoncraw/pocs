import { Stack } from "expo-router";
import { Provider } from 'jotai'

export default function RootLayout() {
  return (
    <Provider testID="jotai-provider">
      <Stack testID="stack">
        <Stack.Screen testID="stack-screen-(tabs)" name="(tabs)" options={{ headerShown: true, title: 'Cutlistener' }} />
      </Stack>
    </Provider>
  );
}
