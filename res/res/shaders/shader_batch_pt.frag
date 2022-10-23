#version 150 core

precision mediump float;

uniform sampler2D textureSampler[16];

in vec2 passTexCoord;
in float passTextureIndex;

out vec4 outColor;

void main() {
	int textureIndex = int(passTextureIndex);
	vec4 color = texture(textureSampler[textureIndex], passTexCoord);
	
	outColor = color; 
}