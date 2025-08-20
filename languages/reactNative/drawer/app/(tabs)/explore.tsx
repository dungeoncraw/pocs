import {GLView} from 'expo-gl';
import {View} from 'react-native';

export default function TabTwoScreen() {
    return (
        <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
            <GLView style={{width: 300, height: 300}} onContextCreate={onContextCreate}/>
        </View>
    );
}

function onContextCreate(gl: {
    viewport: (arg0: number, arg1: number, arg2: any, arg3: any) => void;
    drawingBufferWidth: any;
    drawingBufferHeight: any;
    clearColor: (arg0: number, arg1: number, arg2: number, arg3: number) => void;
    createShader: (arg0: any) => any;
    VERTEX_SHADER: any;
    shaderSource: (arg0: any, arg1: string) => void;
    compileShader: (arg0: any) => void;
    FRAGMENT_SHADER: any;
    createProgram: () => any;
    attachShader: (arg0: any, arg1: any) => void;
    linkProgram: (arg0: any) => void;
    useProgram: (arg0: any) => void;
    clear: (arg0: any) => void;
    COLOR_BUFFER_BIT: any;
    drawArrays: (arg0: any, arg1: number, arg2: number) => void;
    POINTS: any;
    flush: () => void;
    endFrameEXP: () => void;
}) {
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight);
    gl.clearColor(0, 1, 1, 1);

    // Create a vertex shader (shape & position)
    // gl_Position = vec4(0.0, 0.0, 0.0, 1.0)
    // first align on x, second align on y, not clear about third and fourth
    // gl_PointSize = 150.0;
    // size of the square

    const vert = gl.createShader(gl.VERTEX_SHADER);
    gl.shaderSource(
        vert,
        `
    void main(void) {
      gl_Position = vec4(0.5, 0.5, 0.0, 1.0);
      gl_PointSize = 150.0;
    }
  `
    );
    gl.compileShader(vert);

    // Create a fragment shader (color)
    const frag = gl.createShader(gl.FRAGMENT_SHADER);
    gl.shaderSource(
        frag,
        `
    void main(void) {
      gl_FragColor = vec4(10.0, 10.0, 10.0, 1.0);
    }
  `
    );
    gl.compileShader(frag);

    // Link together into a program
    const program = gl.createProgram();
    gl.attachShader(program, vert);
    gl.attachShader(program, frag);
    gl.linkProgram(program);
    gl.useProgram(program);

    gl.clear(gl.COLOR_BUFFER_BIT);
    gl.drawArrays(gl.POINTS, 0, 1);

    gl.flush();
    gl.endFrameEXP();
}