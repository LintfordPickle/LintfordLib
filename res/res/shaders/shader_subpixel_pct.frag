#version 150 core
precision mediump float;

uniform sampler2D textureSampler;

uniform vec2 v2ScreenResolution;
uniform vec2 v2CameraResolution;
uniform float fPixelSize = 1.;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

void main() {
	vec2 v2TextureSize = textureSize(textureSampler, 0);
	
	vec2 texelsPerPixel = v2CameraResolution / v2ScreenResolution / fPixelSize;

	vec2 locationWithinTexel = fract(passTexCoord * v2TextureSize);

	vec2 interpolationAmount = clamp(locationWithinTexel / texelsPerPixel, 0., .5) + clamp((locationWithinTexel - 1.) / texelsPerPixel + .5, 0., .5);
	vec2 finalTextureCoords = (floor(passTexCoord * v2TextureSize) + interpolationAmount) / v2TextureSize;

	outColor = texture(textureSampler, finalTextureCoords);
	
}