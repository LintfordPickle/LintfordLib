#version 460 core

precision mediump float;

in vec4 passColor;

out vec4 outColor;

void main() {
	outColor = passColor; 
}