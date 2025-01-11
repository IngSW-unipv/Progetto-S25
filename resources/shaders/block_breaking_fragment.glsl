#version 330 core

in vec2 pass_texCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform float breakProgress;

void main() {
    vec4 texColor = texture(textureSampler, pass_texCoord);
    float darkening = 1.0 - (0.7 * breakProgress); // Gradually darkens to 0.3 of original brightness
    fragColor = vec4(texColor.rgb * darkening, texColor.a);
}