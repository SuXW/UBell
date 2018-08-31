attribute vec4 a_position;
attribute vec2 a_texCoord;

varying   vec2 v_texCoord;

uniform   mat4 u_MVPMatrix;

void main () {
    v_texCoord = a_texCoord;
    gl_Position = u_MVPMatrix * (a_position) ;
}