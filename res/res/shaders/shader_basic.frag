#version 150 core

uniform sampler2D textureSampler;

in vec2 passTexCoord;
out vec4 outColor;

void main() {
	vec4 color = texture(textureSampler, passTexCoord);
	outColor = color;
	
}