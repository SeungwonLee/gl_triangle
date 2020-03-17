attribute vec4 aPosition;
attribute vec4 aTextureCoord;
uniform mat4 uMVPMatrix;// MVP의 변형 매트릭스 (전체 변형)
uniform mat4 uTexMatrix;// 텍스처 변형 매트릭스 (텍스처 변형 만 해당)

uniform highp float uTexelWidthOffset;
uniform highp float uTexelHeightOffset;
uniform highp float uBlurSize;

varying highp vec2 centerTextureCoordinate;
varying highp vec2 oneStepLeftTextureCoordinate;
varying highp vec2 twoStepsLeftTextureCoordinate;
varying highp vec2 oneStepRightTextureCoordinate;
varying highp vec2 twoStepsRightTextureCoordinate;

//const float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);

void main() {
    vec4 temp_v = uMVPMatrix * aPosition;
    gl_Position = temp_v;

    vec2 firstOffset = vec2(1.3846153846 * uTexelWidthOffset, 1.3846153846 * uTexelHeightOffset) * uBlurSize;
    vec2 secondOffset = vec2(3.2307692308 * uTexelWidthOffset, 3.2307692308 * uTexelHeightOffset) * uBlurSize;

    centerTextureCoordinate = (uTexMatrix * aTextureCoord).xy;
    oneStepLeftTextureCoordinate = centerTextureCoordinate - firstOffset;
    twoStepsLeftTextureCoordinate = centerTextureCoordinate - secondOffset;
    oneStepRightTextureCoordinate = centerTextureCoordinate + firstOffset;
    twoStepsRightTextureCoordinate = centerTextureCoordinate + secondOffset;
}
