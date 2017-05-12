precision mediump float;
varying vec4 v_Color;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;
void main() {
    gl_FragColor = texture2D(sTexture,vTextureCoord);
}
