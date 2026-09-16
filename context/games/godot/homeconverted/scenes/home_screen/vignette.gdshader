shader_type canvas_item;

// Recreates the CSS "radial-gradient(ellipse ... transparent, rgba(void))" vignette.
uniform vec4 edge_color : source_color = vec4(0.024, 0.016, 0.043, 1.0);
uniform float inner_radius : hint_range(0.0, 1.0) = 0.40;
uniform float outer_radius : hint_range(0.0, 1.5) = 0.85;

void fragment() {
	float d = distance(UV, vec2(0.5, 0.46));
	float a = smoothstep(inner_radius, outer_radius, d);
	COLOR = vec4(edge_color.rgb, a * edge_color.a);
}
