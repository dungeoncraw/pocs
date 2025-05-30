import {View, StyleSheet, Platform} from "react-native";
import {useState, useRef} from "react";
import {captureRef} from 'react-native-view-shot';
import * as MediaLibrary from 'expo-media-library';
import * as ImagePicker from 'expo-image-picker';
import {type ImageSource} from 'expo-image';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import domtoimage from 'dom-to-image';

import ImageViewer from "@/components/ImageViewer";
import Button from "@/components/Button";
import IconButton from '@/components/IconButton';
import CircleButton from '@/components/CircleButton';
import {ReactNode} from "react";
import EmojiPicker from '@/components/EmojiPicker';
import EmojiList from "@/components/EmojiList";
import EmojiSticker from "@/components/EmojiSticker";

interface EmojiPickerProps {
    isVisible: boolean;
    onClose: () => void;
    children?: ReactNode;
}

const PlaceholderImage = require('@/assets/images/placeholder.jpg');

export default function Index() {
    const imageRef = useRef<View>(null);
    const [status, requestPermission] = MediaLibrary.usePermissions();
    const [selectedImage, setSelectedImage] = useState<string | undefined>(undefined);
    const [showAppOptions, setShowAppOptions] = useState<boolean>(false);
    const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
    const [pickedEmoji, setPickedEmoji] = useState<ImageSource | undefined>(undefined);

    const pickImageAsync = async () => {
        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            quality: 1,
        });

        if (!result.canceled) {
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
        setIsModalVisible(true);
    };

    const onModalClose = () => {
        setIsModalVisible(false);
    };
    if (status === null) {
        requestPermission();
    }
    const onSaveImageAsync = async () => {
        if (Platform.OS !== 'web') {
            try {
                const localUri = await captureRef(imageRef, {
                    height: 400,
                    quality: 1
                });
                await MediaLibrary.createAssetAsync(localUri);
                if (localUri) {
                    alert('Image saved to MediaLibrary!');
                }
            } catch (e) {
                // TODO this can be better
                console.error(e);
            }
        } else {
            try {
                const dataUrl = await domtoimage.toJpeg(imageRef.current, {
                    quality: 0.95,
                    width: 320,
                    height: 440,
                });

                let link = document.createElement('a');
                link.download = 'sticker-smash.jpeg';
                link.href = dataUrl;
                link.click();
            } catch (e) {
                console.log(e);
            }
        }
    }
    return (
        <GestureHandlerRootView
            style={styles.container}
        >
            <View style={styles.imageContainer}>
                <View ref={imageRef} collapsable={false}>
                    <ImageViewer source={PlaceholderImage} selectedImage={selectedImage}/>
                    {pickedEmoji && <EmojiSticker imageSize={40} stickerSource={pickedEmoji}/>}
                </View>
            </View>
            {showAppOptions ? (
                <View style={styles.optionsContainer}>
                    <View style={styles.optionsRow}>
                        <IconButton icon="refresh" label="Reset" onPress={onReset}/>
                        <CircleButton onPress={onAddSticker}/>
                        <IconButton icon="save-alt" label="Save" onPress={onSaveImageAsync}/>
                    </View>
                </View>
            ) : (
                <View style={styles.footerContainer}>
                    <Button theme="primary" label="Choose a photo" onPress={pickImageAsync}/>
                    <Button label="Use this photo" onPress={() => setShowAppOptions(true)}/>
                </View>
            )}
            <EmojiPicker isVisible={isModalVisible} onClose={onModalClose}>
                <EmojiList onSelect={setPickedEmoji} onCloseModal={onModalClose}/>
            </EmojiPicker>
        </GestureHandlerRootView>
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
        flex: 1 / 3,
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
