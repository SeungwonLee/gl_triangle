uniform mat4 uMVPMatrix;// MVP의 변형 매트릭스 (전체 변형)
uniform mat4 uTexMatrix;// 텍스처 변형 매트릭스 (텍스처 변형 만 해당)
uniform mat4 uProjectionMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;

void main() {
    vec4 temp_v = uMVPMatrix * aPosition;
    gl_Position = uProjectionMatrix * temp_v;
    vTextureCoord = (uTexMatrix * aTextureCoord).xy;
}

