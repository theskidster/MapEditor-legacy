#version 330 core

in vec3 ioColor;
in float ioSelected;

uniform int uType;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: case 2: case 3:
            ioResult = vec4(ioColor, 0);
            break;

        case 1:
            if(ioSelected > 0) {
                ioResult = vec4(0, 0, 1, 0);
            } else {
                ioResult = vec4(ioColor, 0);
            }
            break;
    }
}