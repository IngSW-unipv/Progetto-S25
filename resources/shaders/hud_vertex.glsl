#version 330 core

// Input attribute for the vertex position
layout(location = 0) in vec2 position;

void main() {
    // Set the final position of the vertex in clip space
    gl_Position = vec4(position, 0.0, 1.0);
}
