shader_type canvas_item;

uniform vec4 glow_color : source_color = vec4(0.161, 0.839, 0.902, 0.5);

void fragment() {
	float d = distance(UV, vec2(0.5));
	float a = 1.0 - smoothstep(0.0, 0.5, d);
	COLOR = vec4(glow_color.rgb, glow_color.a * a);
}
