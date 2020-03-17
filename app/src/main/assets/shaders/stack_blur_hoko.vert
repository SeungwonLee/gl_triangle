attribute vec4 aTextureCoord;
attribute vec4 aPosition;
varying vec2 vTexCoord;

uniform mat4 uMVPMatrix;// MVP의 변형 매트릭스 (전체 변형)
uniform mat4 uTexMatrix;// 텍스처 변형 매트릭스 (텍스처 변형 만 해당)

void main() {
    vec4 temp_v = uMVPMatrix * aPosition;
    gl_Position = temp_v;
    vTexCoord = (uTexMatrix * aTextureCoord).xy;
}
// Hokoblur