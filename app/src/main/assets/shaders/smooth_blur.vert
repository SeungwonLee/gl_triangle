#define SAMPLES 7

uniform mat4 uMVPMatrix;// MVP의 변형 매트릭스 (전체 변형)
uniform mat4 uTexMatrix;// 텍스처 변형 매트릭스 (텍스처 변형 만 해당)

uniform float uTexelWidthOffset;
uniform float uTexelHeightOffset;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying float vTexelWidthOffset;
varying float vTexelHeightOffset;

varying vec2 vTextureCoord;
varying vec2 vBlurTextureCoord[SAMPLES];

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = (uTexMatrix * aTextureCoord).xy;
    vTexelWidthOffset = uTexelWidthOffset;
    vTexelHeightOffset = uTexelHeightOffset;

    vec2 offset = vec2(uTexelHeightOffset, uTexelWidthOffset);// horizontal, vertical

    vBlurTextureCoord[0] = vTextureCoord.xy;
    vBlurTextureCoord[1] = vTextureCoord.xy + offset * 1.498702;
    vBlurTextureCoord[2] = vTextureCoord.xy - offset * 1.498702;
    vBlurTextureCoord[3] = vTextureCoord.xy + offset * 3.496973;
    vBlurTextureCoord[4] = vTextureCoord.xy - offset * 3.496973;
    vBlurTextureCoord[5] = vTextureCoord.xy + offset * 5.495243;
    vBlurTextureCoord[6] = vTextureCoord.xy - offset * 5.495243;
}
