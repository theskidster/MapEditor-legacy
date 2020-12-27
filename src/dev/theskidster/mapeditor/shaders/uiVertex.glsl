#version 330 core

layout (location = 0) in vec2 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec4 aColor;

uniform mat4 uProjection;

out vec2 ioTexCoords;
out vec4 ioColor;

void main() {
    ioTexCoords = aTexCoords;
    ioColor     = aColor;
    gl_Position = uProjection * vec4(aPosition.xy, 0, 1);
}