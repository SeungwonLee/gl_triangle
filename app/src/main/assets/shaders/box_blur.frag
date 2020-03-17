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
        float boxWeight = float(1.0) / float(diameter);
        col += sampleTex.rgb * boxWeight;
        weightSum += boxWeight;
    }
    gl_FragColor = vec4(col / weightSum, sampleTex.a);
}
