#version 330 core

// Input vertex attributes
layout(location = 0) in vec3 position;  // Vertex position (x, y, z)
layout(location = 1) in vec2 texCoord;  // Texture coordinates (u, v)

// Output variables to pass to the fragment shader
out vec2 pass_texCoord;  // Pass the texture coordinates to the fragment shader

// Uniform matrices for transformations
uniform mat4 modelMatrix;      // Model transformation matrix
uniform mat4 viewMatrix;       // View transformation matrix
uniform mat4 projectionMatrix; // Projection transformation matrix

void main() {
    // Apply the model, view, and projection transformations to the vertex position
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);

    // Pass the texture coordinates to the fragment shader
    pass_texCoord = texCoord;
}
