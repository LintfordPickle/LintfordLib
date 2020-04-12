#version 150 core
precision mediump float;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;

uniform float fGlobalTime;
uniform vec2 v2Resolution;

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
	vec2 uv = (gl_FragCoord.xy-.5*v2Resolution.xy)/v2Resolution.y;
	
	uv = abs(uv);
	uv.xy *= rot(fGlobalTime);
	
	outColor = vec4(uv.x, uv.y, sin(time), 1.);
	
}