uniform sampler2D uTexture;
varying vec2 vTextureCoord;

uniform float offset[3] = float[](0.0, 1.3846153846, 3.2307692308);
uniform float weight[3] = float[](0.2270270270, 0.3162162162, 0.0702702703);

void main(void)
{
    vec3 sum = vec3(0.0);
    vec4 fragColor = texture2D(uTexture, vec2(vTextureCoord)/1024.0);

    for (int i=1; i<3; i++) {
        sum += texture2D(image, (vec2(vTextureCoord)+vec2(offset[i], 0.0))/1024.0) * weight[i];
        sum += texture2D(image, (vec2(vTextureCoord)-vec2(offset[i], 0.0))/1024.0) * weight[i];
    }
    gl_FragColor = vec4(sum, fragColor.a);
}
