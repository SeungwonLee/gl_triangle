precision highp float;

uniform lowp sampler2D uTexture;

varying highp vec2 centerTextureCoordinate;
varying highp vec2 oneStepLeftTextureCoordinate;
varying highp vec2 twoStepsLeftTextureCoordinate;
varying highp vec2 oneStepRightTextureCoordinate;
varying highp vec2 twoStepsRightTextureCoordinate;

//const float weight[3] = float[]( 0.2270270270, 0.3162162162, 0.0702702703 ); 

void main() {
    lowp vec4 color = texture2D(uTexture, centerTextureCoordinate) * 0.2270270270;
    color += texture2D(uTexture, oneStepLeftTextureCoordinate) * 0.3162162162;
    color += texture2D(uTexture, oneStepRightTextureCoordinate) * 0.3162162162;
    color += texture2D(uTexture, twoStepsLeftTextureCoordinate) * 0.0702702703;
    color += texture2D(uTexture, twoStepsRightTextureCoordinate) * 0.0702702703;
    gl_FragColor = color;
}
