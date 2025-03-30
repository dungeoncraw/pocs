import { StyleSheet } from 'react-native';
import { Image, type ImageSource} from "expo-image";

type Props = {
  source: ImageSource;
};

export default function ImageViewer({source}: Props) {
    return <Image source={source} style={styles.image} />;
}

const styles = StyleSheet.create({
  image: {
    width: 320,
    height: 440,
      borderRadius: 18,
  },
});