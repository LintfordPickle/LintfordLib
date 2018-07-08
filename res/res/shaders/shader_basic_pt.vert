#version 150 core

// uniforms
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

// Attributes
in vec4 inPosition;
in vec2 inTexCoord;

out vec2 passTexCoord;

void main() {
	// col-maj mats
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * inPosition;
	
	passTexCoord  = inTexCoord;
	
}