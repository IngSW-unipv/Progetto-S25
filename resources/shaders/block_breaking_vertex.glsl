#version 330 core

// Input vertex position and texture coordinates
layout(location = 0) in vec3 position;   // The position of the vertex (x, y, z)
layout(location = 1) in vec2 texCoord;   // The texture coordinates (u, v)

// Output texture coordinates to the fragment shader
out vec2 pass_texCoord;

// Uniform matrices for model, view, and projection transformations
uniform mat4 modelMatrix;   // The model transformation matrix (object space to world space)
uniform mat4 viewMatrix;    // The view transformation matrix (world space to camera space)
uniform mat4 projectionMatrix;  // The projection matrix (camera space to clip space)

void main() {
    // Calculate the final position of the vertex by applying the model, view, and projection matrices
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);

    // Pass the texture coordinates to the fragment shader
    pass_texCoord = texCoord;
}
