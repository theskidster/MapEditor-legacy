#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec3 aColor;

//Instanced attributes
layout (location = 2) in vec3 aPosOffset;
layout (location = 3) in vec3 aColOffset;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform int uType;
uniform vec3 uColor;
uniform float uSelected;

out vec3 ioColor;
out float ioSelected;

void main() {
    switch(uType) {
        case 0: //Used for the world origin indicator
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;

        case 1:
            ioColor     = vec3(1);
            ioSelected  = uSelected;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;

        case 2: //Used for test object
            ioColor     = aColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;

        case 3: //Used for displaying map cells (TEMP)
            ioColor     = aColOffset;
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1.0f);
            break;
    }
}