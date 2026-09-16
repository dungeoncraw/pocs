shader_type canvas_item;

uniform vec4 bg_color : source_color = vec4(0.043, 0.043, 0.078, 1.0);

void fragment() {
	float y = UV.y; // 0 = top, 1 = bottom
	float a;
	if (y > 0.5) {
		a = mix(0.55, 1.0, (y - 0.5) / 0.5);
	} else {
		a = mix(0.2, 0.55, y / 0.5);
	}
	COLOR = vec4(bg_color.rgb, a);
}
