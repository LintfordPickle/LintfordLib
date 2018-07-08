#version 150 core

// uniforms
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

// Attributes
in vec4 inPosition;
in vec4 inColor;

out vec4 passColor;

void main() {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * inPosition;
	passColor  = inColor;
}