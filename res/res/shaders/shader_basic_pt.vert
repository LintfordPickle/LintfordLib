#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 inPosition;
in vec2 inTexCoord;

out vec2 passTexCoord;

void main() {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * inPosition;
	
	passTexCoord  = inTexCoord;
	
}