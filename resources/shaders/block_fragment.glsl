#version 330 core

// Input texture coordinates passed from the vertex shader
in vec2 pass_texCoord;

// Output color of the fragment (pixel)
out vec4 fragColor;

// Uniform variable to sample the texture
uniform sampler2D textureSampler;

void main() {
    // Sample the texture at the provided texture coordinates
    fragColor = texture(textureSampler, pass_texCoord);
}
