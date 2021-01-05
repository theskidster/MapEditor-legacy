#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;

precision mediump float;

//uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    //ioResult = texture(uTexture, ioTexCoords.st) * vec4(ioColor, 0);
    ioResult = vec4(ioColor, 0);
}