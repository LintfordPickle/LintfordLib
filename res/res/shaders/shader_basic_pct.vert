#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 inPosition;
in vec4 inColor;
in vec2 inTexCoord;

out vec4 passColor;
out vec2 passTexCoord;

void main() {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * inPosition;
	
	passColor = inColor;
	
	passTexCoord  = inTexCoord;
	
}