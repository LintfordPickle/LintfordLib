#version 150 core

uniform sampler2D textureSampler;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

void main() {
	vec4 color = texture(textureSampler, passTexCoord);
	outColor = color * passColor;
	
}