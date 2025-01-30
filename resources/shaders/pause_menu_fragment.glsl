// pause_menu_fragment.glsl
#version 330 core

out vec4 fragColor;
uniform vec4 overlayColor;

void main() {
    fragColor = overlayColor;
}