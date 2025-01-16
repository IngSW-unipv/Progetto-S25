#version 330 core

// Output color of the fragment (pixel)
out vec4 fragColor;

void main() {
    // Set the fragment color to a semi-transparent black
    // This gives a visible black border effect
    fragColor = vec4(0.0, 0.0, 0.0, 0.4);  // RGB black with 40% opacity (alpha)
}
