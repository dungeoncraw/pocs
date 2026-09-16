shader_type canvas_item;

// Recreates the CSS "radial-gradient dot grid masked into a corner" effect.
uniform vec2 rect_size = vec2(900.0, 900.0);
uniform float spacing : hint_range(10.0, 100.0) = 34.0;
uniform float dot_radius : hint_range(0.5, 6.0) = 1.6;
uniform vec4 dot_color : source_color = vec4(0.184, 0.965, 1.0, 0.55);
uniform vec2 mask_center = vec2(0.65, 0.35); // in UV space (0-1)
uniform float mask_radius : hint_range(0.05, 1.5) = 0.62;

void fragment() {
	vec2 px = UV * rect_size;
	vec2 cell = mod(px, spacing);
	float d = distance(cell, vec2(spacing * 0.5));
	float dot = 1.0 - smoothstep(dot_radius, dot_radius + 1.0, d);

	float mask_d = distance(UV, mask_center);
	float mask = 1.0 - smoothstep(mask_radius * 0.4, mask_radius, mask_d);

	COLOR = vec4(dot_color.rgb, dot_color.a * dot * mask);
}
