#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 inPosition;
in vec4 inColor;

out vec4 passColor;
void main() {

	gl_Position = projectionMatrix * viewMatrix * modelMatrix * inPosition;
	
	passColor = inColor;	
}