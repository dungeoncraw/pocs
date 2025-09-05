import React, {useEffect, useState} from 'react';
import {Canvas} from '@react-three/fiber/native';
import {View, Text, StyleSheet} from 'react-native';
import {Camera, CameraView} from 'expo-camera';
import Teeth from './components/Teeth';

export default function Index() {
    const [hasPermission, setHasPermission] = useState<boolean | null>(null);

    useEffect(() => {
        Camera.requestCameraPermissionsAsync().then(({status}) => {
            setHasPermission(status === 'granted');
        });
    }, []);

    if (hasPermission === null) return null;
    if (hasPermission === false) return (
        <View style={styles.container}>
            <Text style={styles.debugText}>Sem permissão para usar a câmera</Text>
        </View>
    );

    return (
        <View style={styles.container}>
            <CameraView style={StyleSheet.absoluteFillObject} facing="back"/>
            <View style={styles.overlay} pointerEvents="none">
                <Canvas
                    style={styles.canvasContainer}
                    gl={{
                        antialias: true,
                        // turn on alpha for transparent backgrounds
                        alpha: true,
                        // preserve buffer between frames for transparent backgrounds
                        preserveDrawingBuffer: true,
                        // turn off premultiplied alpha for transparent backgrounds
                        premultipliedAlpha: false
                    }}
                    camera={
                        {
                            position: [0, 0, 5],
                            // FOV = field of view, the extent of the scene displayed at any moment
                            fov: 50
                        }
                    }
                    onCreated={({gl}) => {
                        gl.setClearColor(0x000000, 0);
                    }}


                >
                    <ambientLight intensity={Math.PI / 2} color={'yellow'}/>
                    <spotLight position={[10, 10, 10]} angle={0.15} penumbra={1} decay={0} intensity={Math.PI}/>
                    <pointLight position={[-10, -10, -10]} decay={0} intensity={Math.PI}/>
                    <Teeth/>
                </Canvas>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'transparent',
    },
    debugText: {
        color: 'white',
        fontSize: 18,
        textAlign: 'center',
        marginTop: 50,
        zIndex: 1000,
    },
    canvasContainer: {
        flex: 1,
        width: '100%',
        height: '100%',
        backgroundColor: 'transparent',
    },
    overlay: {
        ...StyleSheet.absoluteFillObject,
        pointerEvents: 'none',
        zIndex: 10,
        backgroundColor: 'transparent'

    },
});
