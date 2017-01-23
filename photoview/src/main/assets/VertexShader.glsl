uniform mat4 u_MVPMatrix;
attribute vec4 position;
attribute vec2 texturePosition;

varying vec2 vTextureCoord;

void main() {
     vTextureCoord=texturePosition;
     gl_Position = position;
}