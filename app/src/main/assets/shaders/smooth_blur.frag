#define SAMPLES 7
precision highp float;
uniform sampler2D uTexture;

varying vec2 vTextureCoord;
varying vec2 vBlurTextureCoord[SAMPLES];

varying float vTexelWidthOffset;
varying float vTexelHeightOffset;

void main()
{
    vec4 sum = vec4(0.0);

    vec2 singleStepOffset = vec2(vTexelWidthOffset, vTexelHeightOffset);

    sum += texture2D(uTexture, vBlurTextureCoord[0]) * 0.024856;
    sum += texture2D(uTexture, vBlurTextureCoord[1]) * 0.024856;
    sum += texture2D(uTexture, vBlurTextureCoord[2]) * 0.049497;
    sum += texture2D(uTexture, vBlurTextureCoord[3]) * 0.048649;
    sum += texture2D(uTexture, vBlurTextureCoord[4]) * 0.048649;
    sum += texture2D(uTexture, vBlurTextureCoord[5]) * 0.047159;
    sum += texture2D(uTexture, vBlurTextureCoord[6]) * 0.047159;

    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 7.493513) * 0.045086;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 7.493513) * 0.045086;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 9.491782) * 0.042513;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 9.491782) * 0.042513;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 11.490053) * 0.039536;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 11.490053) * 0.039536;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 13.488324) * 0.036262;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 13.488324) * 0.036262;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 15.486594) * 0.032803;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 15.486594) * 0.032803;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 17.484867) * 0.029266;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 17.484867) * 0.029266;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 19.483137) * 0.025752;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 19.483137) * 0.025752;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 21.481409) * 0.022349;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 21.481409) * 0.022349;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 23.479683) * 0.019129;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 23.479683) * 0.019129;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 25.477955) * 0.016148;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 25.477955) * 0.016148;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 27.476229) * 0.013444;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 27.476229) * 0.013444;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 29.474503) * 0.011040;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 29.474503) * 0.011040;
    sum += texture2D(uTexture, vBlurTextureCoord[0] + singleStepOffset * 31.472776) * 0.008941;
    sum += texture2D(uTexture, vBlurTextureCoord[0] - singleStepOffset * 31.472776) * 0.008941;
    //    sum += texture2D(uTexture, vBlurTextureCoord[6]) * 0.12;
    //    sum += texture2D(uTexture, vBlurTextureCoord[7]) * 0.09;
    //    sum += texture2D(uTexture, vBlurTextureCoord[8]) * 0.05;

    gl_FragColor = sum;
}
