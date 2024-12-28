#version 330 core

out vec4 fragColor;

uniform vec3 lightColor;
uniform vec3 lightDir;

void main() {
    fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Rosso per debug
}