#version 330 core

in vec2 ioTexCoords;
in vec4 ioColor;

precision mediump float;

uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    ioResult = ioColor * texture(uTexture, ioTexCoords.st);
}