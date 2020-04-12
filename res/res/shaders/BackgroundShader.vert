#version 150 core

// uniforms
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform float time;
uniform float random;
uniform float intensity;

// Attributes
in vec4 inPosition;
in vec4 inColor;
in vec2 inTexCoord;

out vec4 passColor;
out vec2 passTexCoord;

void main() {
	vec4 offset = vec4( intensity * (cos( time/1.5 ) * (1.0 - inColor.r)) * sin( inPosition.x + random )    * (1.0 - inColor.r),
                        intensity * (cos( time/2.0 ) * (1.0 - inColor.r)) * sin( inPosition.y + 2.0*random) * (1.0 - inColor.r),
                        0, 
                        0);

	// col-maj mats
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * (inPosition + offset);
	gl_Position;
	
	passColor = inColor;
	
	passTexCoord  = inTexCoord;
	
}