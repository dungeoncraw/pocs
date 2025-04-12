import { View, StyleSheet } from "react-native";
import {useState} from "react";
import * as ImagePicker from 'expo-image-picker';
import ImageViewer from "@/components/ImageViewer";
import Button from "@/components/Button";
import IconButton from '@/components/IconButton';
import CircleButton from '@/components/CircleButton';

const PlaceholderImage = require('@/assets/images/placeholder.jpg');

export default function Index() {
    const [selectedImage, setSelectedImage] = useState<string | undefined>(undefined);
    const [showAppOptions, setShowAppOptions] = useState<boolean>(false);

    const pickImageAsync = async() => {
        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            quality: 1,
        });

        if (!result.canceled){
            setSelectedImage(result.assets[0].uri);
            setShowAppOptions(true);
        } else {
            alert("You didn't pick an image");
        }
    }
    const onReset = () => {
        setShowAppOptions(false);
    };

    const onAddSticker = () => {
        // we will implement this later
    };

    const onSaveImageAsync = async () => {
        // we will implement this later
    };
    return (
        <View
            style={styles.container}
        >
            <View style={styles.imageContainer}>
                <ImageViewer source={PlaceholderImage} selectedImage={selectedImage} />
            </View>
            {showAppOptions ? (
                <View style={styles.optionsContainer}>
                    <View style={styles.optionsRow}>
                        <IconButton icon="refresh" label="Reset" onPress={onReset} />
                        <CircleButton onPress={onAddSticker} />
                        <IconButton icon="save-alt" label="Save" onPress={onSaveImageAsync} />
                    </View>
                </View>
            ) : (
                <View style={styles.footerContainer}>
                    <Button theme="primary" label="Choose a photo" onPress={pickImageAsync}/>
                    <Button label="Use this photo" onPress={() => setShowAppOptions(true)}/>
                </View>
            )}

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
    },
    optionsContainer: {
        position: 'absolute',
        bottom: 80,
    },
    optionsRow: {
        alignItems: 'center',
        flexDirection: 'row',
    },
})
