#version 150 core
precision mediump float;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;

uniform float fGlobalTime;
uniform vec2 v2ScreenResolution;
uniform vec2 v2CameraResolution;
uniform vec2 v2MouseWindowCoords;
uniform float fCameraZoomFactor;

#define time fGlobalTime

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

mat2 rot(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}

void main() {
	vec2 uv = (gl_FragCoord.xy-.5*v2CameraResolution.xy)/v2CameraResolution.y;
	
	uv = abs(uv);
	uv.xy *= rot((v2MouseWindowCoords.x / v2ScreenResolution.x) + (v2MouseWindowCoords.y / v2ScreenResolution.y));
	
	outColor = vec4(uv.xy, 0., 1.);
	
}