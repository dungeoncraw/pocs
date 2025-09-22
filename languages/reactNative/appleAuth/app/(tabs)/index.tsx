import {Platform, StyleSheet} from 'react-native';
import * as AppleAuthentication from 'expo-apple-authentication';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';

export default function HomeScreen() {
  if (Platform.OS !== 'ios') {
    return (
      <ThemedView style={styles.container}>
        <ThemedText>Apple Sign-In is only available on iOS devices.</ThemedText>
      </ThemedView>
    );
  }
  return (
      <ThemedView style={styles.container}>
          <AppleAuthentication.AppleAuthenticationButton
              buttonType={AppleAuthentication.AppleAuthenticationButtonType.SIGN_IN}
              buttonStyle={AppleAuthentication.AppleAuthenticationButtonStyle.BLACK}
              cornerRadius={5}
              style={styles.button}
              onPress={async () => {
                  try {
                      const credential = await AppleAuthentication.signInAsync({
                          requestedScopes: [
                              AppleAuthentication.AppleAuthenticationScope.FULL_NAME,
                              AppleAuthentication.AppleAuthenticationScope.EMAIL,
                          ],
                      });
                      console.log(credential);
                  } catch (e) {
                      // @ts-ignore
                      if (e?.code === 'ERR_REQUEST_CANCELED') {
                          console.log("user cancelled");
                      } else {
                          console.log("unknown error", e);
                      }
                  }
              }}
          />
      </ThemedView>

  );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    button: {
        width: 200,
        height: 44,
    },
});
