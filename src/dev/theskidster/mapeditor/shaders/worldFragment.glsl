#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;

uniform int uType;
uniform sampler2D uTexture;

out vec4 ioResult;

void makeTransparent(float a) {
    if(a == 0) discard;
}

void main() {
    switch(uType) {
        case 0: case 1: //Used for the world origin indicator and floor tiles
            ioResult = vec4(ioColor, 0);
            break;

        case 2: //Used for level geometry
            ioResult = vec4(ioColor, 0);
            break;

        case 3: //Used for light source icons
            makeTransparent(texture(uTexture, ioTexCoords).a);
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, 1);
            break;
    }
}