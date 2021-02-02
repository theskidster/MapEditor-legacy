#version 330 core

in vec3 ioColor;

uniform int uType;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: case 1: //Used for the world origin indicator and floor tiles
            ioResult = vec4(ioColor, 0);
            break;

        case 2: //Used for level geometry
            
            break;
    }
}