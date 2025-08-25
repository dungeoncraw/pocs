import React from 'react';
import { useGLTF } from "@react-three/drei";

export default function Teeth() {
    // TODO fix this typescript issue on importing glb
    const { scene, nodes, materials } = useGLTF(require('../../assets/models/free_teeth_base_mesh.glb'));

    if (!scene) return null;

    return (
        <group position={[-0.42, 0.51, -0.62]} rotation={[Math.PI / 2, 0, 0]}>
            <primitive object={scene} />
        </group>
    );
}