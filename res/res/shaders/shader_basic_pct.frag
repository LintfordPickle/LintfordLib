#version 330 core

precision mediump float;

uniform sampler2D textureSampler;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

void main() {
	vec4 color = texture(textureSampler, passTexCoord);
	outColor = passColor * color;
}