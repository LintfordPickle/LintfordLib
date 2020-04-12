#version 150 core
precision mediump float;

uniform sampler2D textureSampler;

in vec4 passColor;
in vec2 passTexCoord;

out vec4 outColor;

void main() {
	vec4 color = texture(textureSampler, passTexCoord);
	outColor = color * passColor;
	
}