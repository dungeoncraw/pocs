import React from 'react';
import { useGLTF } from "@react-three/drei";

export default function Teeth() {
    const { scene, nodes, materials } = useGLTF('/assets/models/free_teeth_base_mesh.glb') as any;

    if (!scene) return null;
    console.log(scene);
    console.log(nodes);
    console.log(materials);
    return (
        <>
            <primitive object={nodes.Cube001} position={[0, 0, 0]} scale={[0.3, 0.3, 0.3]}/>
            <primitive object={nodes.Cube002} position={[2, 0, 0]} scale={[0.3, 0.3, 0.3]}/>
            <primitive object={nodes.Cube003} position={[-2, 0, 0]} scale={[0.3, 0.3, 0.3]}/>
        </>
    );
}