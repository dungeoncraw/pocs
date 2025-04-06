import { View, StyleSheet } from "react-native";
import {useState} from "react";
import * as ImagePicker from 'expo-image-picker';
import ImageViewer from "@/components/ImageViewer";
import Button from "@/components/Button";

const PlaceholderImage = require('@/assets/images/placeholder.jpg');

export default function Index() {
    const [selectedImage, setSelectedImage] = useState<string | undefined>(undefined);

    const pickImageAsync = async() => {
        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            quality: 1,
        });

        if (!result.canceled){
            setSelectedImage(result.assets[0].uri);
        } else {
            alert("You didn't pick an image");
        }
    }
    return (
        <View
            style={styles.container}
        >
            <View style={styles.imageContainer}>
                <ImageViewer source={PlaceholderImage} selectedImage={selectedImage} />
            </View>
            <View style={styles.footerContainer}>
                <Button theme="primary" label="Choose a photo" onPress={pickImageAsync}/>
                <Button label="Use this photo" />
            </View>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#25292e',
        alignItems: 'center',
    },
    imageContainer: {
        flex: 1
    },
    image: {
        width: 320,
        height: 440,
        borderRadius: 18,
    },
    footerContainer: {
        flex: 1 /3,
        alignItems: 'center',
    }
})
