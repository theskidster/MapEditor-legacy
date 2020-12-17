#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3  aPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform int uType;

void main() {
    switch(uType) {
        case 0:
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;
    }
}