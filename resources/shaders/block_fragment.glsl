#version 330 core

in vec2 pass_texCoord;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    fragColor = texture(textureSampler, pass_texCoord);
}