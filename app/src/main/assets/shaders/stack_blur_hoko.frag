precision mediump float;
varying vec2 vTexCoord;
uniform sampler2D uTexture;
uniform int uBlurSize;
uniform float uTexelWidthOffset;
uniform float uTexelHeightOffset;

void main() {
    int diameter = 2 * uBlurSize + 1;
    vec4 sampleTex = vec4(0, 0, 0, 0);
    vec3 col = vec3(0, 0, 0);
    float weightSum = 0.0;
    for (int i = 0; i < diameter; i++) {
        vec2 offset = vec2(float(i - uBlurSize) * uTexelWidthOffset, float(i - uBlurSize) * uTexelHeightOffset);
        sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));
        float index = float(i);
        float boxWeight = float(uBlurSize) + 1.0 - abs(index - float(uBlurSize));
        col += sampleTex.rgb * boxWeight;
        weightSum += boxWeight;
    }
    gl_FragColor = vec4(col / weightSum, sampleTex.a);
}
// Hokoblur
// https://github.com/HokoFly/HokoBlur-Kotlin/blob/1abc3a597ff9bddb6cac32e37f2045392e2ad9ae/library/src/main/java/com/hoko/ktblur/util/ShaderUtil.kt