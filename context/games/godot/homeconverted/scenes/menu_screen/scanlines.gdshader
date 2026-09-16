shader_type canvas_item;
render_mode blend_add; // approximates CSS mix-blend-mode: screen

// Recreates the CSS .scanlines utility:
// repeating-linear-gradient(to bottom, transparent 0, transparent 2px, tint 3px, transparent 4px)
uniform vec4 line_color : source_color = vec4(0.161, 0.839, 0.902, 0.06); // --glow-cyan at ~6%
uniform float rect_height = 800.0;

void fragment() {
	float y = UV.y * rect_height;
	float m = mod(y, 4.0);
	float a = (m >= 2.0 && m < 3.0) ? line_color.a : 0.0;
	COLOR = vec4(line_color.rgb, a);
}
