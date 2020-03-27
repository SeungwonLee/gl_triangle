// https://learnopengl.com/Advanced-Lighting/Bloom
#define ARRAY_CNT 5
precision mediump float;
uniform sampler2D uTexture;

varying vec2 vTextureCoord;

uniform float uTexelWidthOffset;
uniform float uTexelHeightOffset;
void main()
{
    vec2 offset = vec2(uTexelHeightOffset, uTexelWidthOffset);// horizontal, vertical
    // weight kernal size=3x3, sigma=1
    // sample count = 9
    // [http://dev.theomader.com/gaussian-kernel-calculator/]
    float weight[ARRAY_CNT];
    weight[0] = 0.195346;
    weight[1] = 0.123317;
    weight[2] = 0.077847;
    weight[3] = 0.123317;
    weight[4] = 0.077847;

    vec3 sum = texture2D(uTexture, vTextureCoord).rgb * weight[0];

    for (int i = 1; i < ARRAY_CNT; i++) {
        sum += texture2D(uTexture, vTextureCoord + vec2(float(i) * offset)).rgb * weight[i];
        sum += texture2D(uTexture, vTextureCoord- vec2(float(i) * offset)).rgb * weight[i];
    }

    gl_FragColor = vec4(sum, 1.0);
}
