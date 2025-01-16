#version 330 core

// Input vertex attribute
layout(location = 0) in vec3 position;  // Vertex position (x, y, z)

// Uniform matrices for transformations
uniform mat4 modelMatrix;      // Model transformation matrix
uniform mat4 viewMatrix;       // View transformation matrix
uniform mat4 projectionMatrix; // Projection transformation matrix

void main() {
    // Expand the block slightly to avoid z-fighting (depth issues)
    vec3 expanded = position * 1.002;  // Slightly scale the position to prevent depth conflicts

    // Apply the model, view, and projection transformations to the vertex position
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(expanded, 1.0);
}
