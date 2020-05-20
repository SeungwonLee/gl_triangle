// https://learnopengl.com/Advanced-Lighting/Bloom
#define ARRAY_CNT 5
precision mediump float;
uniform sampler2D uTexture;

varying vec2 vTextureCoord;

uniform float uTexelWidthOffset;
uniform float uTexelHeightOffset;

uniform float limitStartX;
uniform float limitStartY;

uniform float limitEndX;
uniform float limitEndY;

vec3 blur() {
    vec3 sum = vec3(0.0, 0.0, 0.0);
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

    sum = texture2D(uTexture, vTextureCoord).rgb * weight[0];
    for (int i = 1; i < ARRAY_CNT; i++) {
        sum += texture2D(uTexture, vTextureCoord + vec2(float(i) * offset)).rgb * weight[i];
        sum += texture2D(uTexture, vTextureCoord - vec2(float(i) * offset)).rgb * weight[i];
    }
    return sum;
}

void main()
{
    //    if (vTextureCoord.x > 0.1 && vTextureCoord.y > 0.0 && vTextureCoord.x < 0.5 && vTextureCoord.y < 0.5) {
    //        vec3 sum = blur();
    //        gl_FragColor = vec4(sum, 1.0);
    //    } else if (vTextureCoord.x > 0.5 && vTextureCoord.y > 0.0 && vTextureCoord.x < 0.9 && vTextureCoord.y < 0.9) {
    //        vec3 sum = blur();
    //        gl_FragColor = vec4(sum, 1.0);
    if (vTextureCoord.s > limitStartX && vTextureCoord.t > limitStartY &&
    vTextureCoord.s < limitEndX && vTextureCoord.t < limitEndY) {
        vec3 sum = blur();
        gl_FragColor = vec4(sum, 1.0);
    } else {
        gl_FragColor = texture2D(uTexture, vTextureCoord);
    }
}
