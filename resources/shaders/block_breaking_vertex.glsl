// block_breaking_vertex.glsl  
#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in float lightLevel;

out vec2 pass_texCoord;
out float pass_LightIntensity;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    pass_texCoord = texCoord;
    pass_LightIntensity = lightLevel;
}