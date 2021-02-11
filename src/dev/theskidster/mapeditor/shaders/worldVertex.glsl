#version 330 core

//Non-instanced attributes
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;

//Instanced attributes
layout (location = 4) in vec3 aPosOffset;
layout (location = 5) in vec3 aColOffset;

uniform int uType;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform vec3 uColor;

out vec2 ioTexCoords;
out vec3 ioColor;
out vec3 ioNormal;
out vec3 ioFragPos;

void main() {
    switch(uType) {
        case 0: //Used for the world origin indicator
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;

        case 1: //Used for displaying floor tiles
            ioColor     = aColOffset;
            gl_Position = uProjection * uView * uModel * vec4(aPosition + aPosOffset, 1.0f);
            break;

        case 2: //Used for level geometry
            ioNormal    = aNormal;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;
            
        case 3: //Used for light source icons
            ioTexCoords = aTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;

        case 4: //Used for cube test object
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0f);
            break;
    }
}