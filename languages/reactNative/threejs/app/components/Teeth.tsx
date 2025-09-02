import React, { useRef, useEffect } from 'react';
import { useGLTF } from "@react-three/drei";
import { useFrame } from "@react-three/fiber";
import * as THREE from 'three';

export default function Teeth() {
    const { scene } = useGLTF(require('../../assets/models/teeth.glb'));
    const groupRef = useRef<THREE.Group>(null);

    useEffect(() => {
        if (groupRef.current) {
            groupRef.current.position.set(0.6, -1, 0);
        }
    }, []);
    // useFrame is a hook that allows you to run code on every frame
    // delta is the time elapsed since the last frame
    useFrame((_, delta) => {
        if (groupRef.current) {
            groupRef.current.rotation.y += delta;
        }
    });
    if (!scene) return null;

    return (
        <>
            {/*rotation x, y, z */}
            <group position={[-1.2, 2, -0.9]} rotation={[0.3, 0.2, 0]}>
                <primitive object={scene} />
            </group>
            <group ref={groupRef}>
                {/* clone is needed to avoid the model being duplicated, reusing the same glb file*/}
                <primitive object={scene.clone()}/>
            </group>
        </>

    );
}