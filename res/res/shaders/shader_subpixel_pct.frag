#version 150 core
precision highp float;

uniform sampler2D textureSampler[16];

in vec2 passTexCoord;
in vec4 passColor;
in float passTextureIndex;

out vec4 outColor;

vec2 uv_nearest(vec2 uv, ivec2 texture_size ) {
    vec2 pixel = uv * texture_size;
    pixel = floor(pixel) + .5;

    return pixel / texture_size;
}

vec2 uv_cstantos( vec2 uv, ivec2 res ) {
    vec2 pixels = uv * res;

    // Updated to the final article
    vec2 alpha = 0.7 * fwidth(pixels);
    vec2 pixels_fract = fract(pixels);
    vec2 pixels_diff = clamp( .5 / alpha * pixels_fract, 0, .5 ) +
                       clamp( .5 / alpha * (pixels_fract - 1) + .5, 0, .5 );
    pixels = floor(pixels) + pixels_diff;
    return pixels / res;
}

vec2 uv_klems( vec2 uv, ivec2 texture_size ) {
    vec2 pixels = uv * texture_size + .5;
    
    // tweak fractional value of the texture coordinate
    vec2 fl = floor(pixels);
    vec2 fr = fract(pixels);
    vec2 aa = fwidth(pixels) * .75;

    fr = smoothstep( vec2(0.5) - aa, vec2(.5) + aa, fr);
    
    return (fl + fr - .5) / texture_size;
}

void main() {
	int textureIndex = int(passTextureIndex);
    vec2 uv = passTexCoord;
    ivec2 textureResolution = textureSize(textureSampler[textureIndex], 0);
    
    vec2 pixel = uv_cstantos(uv, textureResolution);
    outColor = texture(textureSampler[textureIndex], pixel);

	outColor *= passColor;
}