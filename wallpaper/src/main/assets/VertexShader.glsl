uniform mat4 u_MVPMatrix;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 aTextureCoord;
varying vec4 v_Color;
varying vec2 vTextureCoord;
void main() {
     v_Color = a_Color;
     vTextureCoord=aTextureCoord;
     gl_Position = u_MVPMatrix  * a_Position;
}