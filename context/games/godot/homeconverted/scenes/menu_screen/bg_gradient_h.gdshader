shader_type canvas_item;

uniform vec4 bg_color : source_color = vec4(0.043, 0.043, 0.078, 1.0);

void fragment() {
	float x = UV.x;
	float d = abs(x - 0.5) * 2.0; // 0 at center, 1 at edges
	COLOR = vec4(bg_color.rgb, 0.7 * d);
}
