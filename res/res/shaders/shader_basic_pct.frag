#version 150 core
precision mediump float;

uniform sampler2D textureSampler;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

void main() {
	vec4 color = texture(textureSampler, passTexCoord, 0);
	outColor = passColor * color;
	
}