#version 330 core

in vec2 TexCoord;
in float LightIntensity;

out vec4 FragColor;

uniform sampler2D textureSampler;
uniform float ambientLight;

void main() {
    vec4 texColor = texture(textureSampler, TexCoord);
    // Combine block light with ambient light
    float finalLight = min(1.0, LightIntensity * ambientLight);
    FragColor = texColor * vec4(vec3(finalLight), 1.0);
}