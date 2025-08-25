import React from 'react'
import {Canvas} from '@react-three/fiber'
import {View, Text, StyleSheet} from 'react-native'
import Teeth from './components/Teeth';

export default function Index() {
    return (
        <View style={styles.container}>
            <Text style={styles.debugText}>Canvas Loading...</Text>
            <Canvas
                style={styles.canvasContainer}
                gl={{antialias: true}}
                camera={
                    {
                        position: [0, 0, 5],
                        // FOV = field of view, the extent of the scene displayed at any moment
                        fov: 50
                    }
                }

            >
                <ambientLight intensity={Math.PI / 2}/>
                <spotLight position={[10, 10, 10]} angle={0.15} penumbra={1} decay={0} intensity={Math.PI}/>
                <pointLight position={[-10, -10, -10]} decay={0} intensity={Math.PI}/>
                <Teeth/>
            </Canvas>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#000',
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

    },
})
