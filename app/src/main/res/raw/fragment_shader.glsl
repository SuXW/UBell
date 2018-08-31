#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2      v_texCoord;

uniform sampler2D u_texture;

void main () {
    gl_FragColor = texture2D(u_texture, v_texCoord);
    //gl_FragColor = vec4(0.2, 1.0, 0.129, 0);
}