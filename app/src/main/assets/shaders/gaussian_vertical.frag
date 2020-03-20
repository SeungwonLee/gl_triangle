precision highp float;

uniform sampler2D uTexture;
varying vec2 vTextureCoord;

// https://github.com/vapopescu/OpenGLEngine/blob/14f6b7b328d7cd1ad13503bc1597756054193408/src/com/shaders/blur9Y.glsl
//uniform float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);
//uniform float weight[3] = float[](0.2270270270, 0.3162162162, 0.0702702703);
//uniform float weight[3] = {0.2270270270, 0.3162162162, 0.0702702703};
uniform bool isVertical;

void main(void)
{
    vec2 pixel = vec2(0.0, 0.0);
    if (isVertical) {
        pixel = vec2(0.0, 1.0 / 2460.0);
    } else {
        pixel = vec2(1.0 / 1440.0, 0.0);
    }

    float offset[3];
    float weight[3];

    offset[0] = 0.0;
    offset[1] = 1.3846153846;
    offset[2] = 3.2307692308;

    weight[0] = 0.2270270270;
    weight[1] = 0.3162162162;
    weight[2] = 0.0702702703;

    vec3 sum = texture2D(uTexture, vTextureCoord).rgb * weight[0];

    for (int i = 1; i < 3; i++) {
        sum += texture2D(uTexture, vTextureCoord + offset[i] * pixel).rgb * weight[i];
        sum += texture2D(uTexture, vTextureCoord - offset[i] * pixel).rgb * weight[i];
    }

    gl_FragColor = vec4(sum, 1.0);
}
