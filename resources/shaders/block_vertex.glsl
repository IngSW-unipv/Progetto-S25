#version 330 core

layout(location = 0) in vec3 aPos;       // Posizione del vertice
layout(location = 1) in vec2 aTexCoord;  // Coordinate texture
layout(location = 2) in float aLight;    // Livello di luce normalizzato

out vec2 TexCoord;
out float LightIntensity;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
    LightIntensity = aLight; // Passa l'intensità della luce al fragment shader
}
