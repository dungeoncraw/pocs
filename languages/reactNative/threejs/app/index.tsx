import React, { useRef } from 'react'
import { Canvas, useFrame } from '@react-three/fiber/native'
import { View, Text, StyleSheet } from 'react-native'
import { Mesh } from 'three'

function Box() {
  const meshRef = useRef<Mesh>(null!)
  
  useFrame((state, delta) => (meshRef.current.rotation.x += delta))
  
  return (
    <mesh ref={meshRef}>
      <boxGeometry args={[1, 1, 1]} />
      <meshStandardMaterial color={'orange'} />
    </mesh>
  )
}

export default function Index() {
  return (
    <View style={styles.container}>
      <Text style={styles.debugText}>Canvas Loading...</Text>
      <Canvas style={styles.canvasContainer}>
        <ambientLight intensity={Math.PI / 2} />
        <spotLight position={[10, 10, 10]} angle={0.15} penumbra={1} decay={0} intensity={Math.PI} />
        <pointLight position={[-10, -10, -10]} decay={0} intensity={Math.PI} />
        <Box />
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
  },
})
