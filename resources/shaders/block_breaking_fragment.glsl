#version 330 core

// Input texture coordinates passed from the vertex shader
in vec2 pass_texCoord;

// Output color of the fragment
out vec4 fragColor;

// Uniforms for texture sampling and break progress
uniform sampler2D textureSampler;  // The texture sampler used to sample the texture
uniform float breakProgress;       // The progress of the break effect (ranges from 0.0 to 1.0)

void main() {
    // Sample the texture color at the given texture coordinates
    vec4 texColor = texture(textureSampler, pass_texCoord);

    // Calculate the darkening factor based on the break progress
    float darkening = 1.0 - (0.7 * breakProgress); // Gradually darkens the texture as breakProgress increases

    // Apply the darkening effect to the texture color and set the fragment color
    fragColor = vec4(texColor.rgb * darkening, texColor.a);
}
