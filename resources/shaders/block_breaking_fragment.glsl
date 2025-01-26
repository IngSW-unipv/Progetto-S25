// block_breaking_fragment.glsl
#version 330 core

in vec2 pass_texCoord;
in float pass_LightIntensity;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform float breakProgress;
uniform float ambientLight;

void main() {
    vec4 texColor = texture(textureSampler, pass_texCoord);
    float currentLight = pass_LightIntensity * ambientLight;
    float minLight = ambientLight * 0.2;  // Luce minima come 20% dell'ambient
    float lightRange = currentLight - minLight;  // Range tra luce attuale e minima
    float darkening = currentLight - (lightRange * breakProgress);  // Oscuramento proporzionale

    fragColor = vec4(texColor.rgb * darkening, texColor.a);
}