import { JSX, Suspense} from 'react'
import {Canvas} from '@react-three/fiber/native'
import {useGLTF} from '@react-three/drei/native'
// @ts-ignore
import modelPath from '../../assets/models/free_teeth_base_mesh.glb'

// @ts-ignore
function Model(props) {
    const gltf = useGLTF(modelPath)
    return <primitive {...props} object={gltf.scene} />
}

export default function TabTwoScreen() {
  return (
      <Canvas>
          <ambientLight />
          <Suspense>
              <Model />
          </Suspense>
      </Canvas>
  );
}