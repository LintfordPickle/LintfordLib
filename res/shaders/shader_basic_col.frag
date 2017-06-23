#version 150 core

uniform sampler2D textureSampler;

in vec4 passColor;

out vec4 outColor;

void main() {
	outColor = passColor;
	
}