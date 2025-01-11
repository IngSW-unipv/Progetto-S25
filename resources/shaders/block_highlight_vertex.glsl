#version 330 core

layout(location = 0) in vec3 position;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    // Espandi leggermente il blocco per evitare z-fighting
    vec3 expanded = position * 1.002;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(expanded, 1.0);
}