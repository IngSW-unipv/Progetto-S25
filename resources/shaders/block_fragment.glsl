#version 330 core

in vec2 TexCoord;
in float LightIntensity; // Intensità della luce dal vertex shader

out vec4 FragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 texColor = texture(textureSampler, TexCoord);
    FragColor = texColor * vec4(vec3(LightIntensity), 1.0); // Applica la luce
}
