#define KERNEL_SIZE 9
#define COLOR_ADJUST 0.5
precision mediump float;

uniform sampler2D s_texture;
varying vec2 v_texCoord;
uniform float uKernel[KERNEL_SIZE];
uniform vec2 uTexOffset[KERNEL_SIZE];

void main()
{
    vec4 sum = vec4(0.0);
    int i = 0;

    for (i = 0; i < KERNEL_SIZE; i++) {
        vec4 texc = texture2D(s_texture, v_texCoord) + uTexOffset[i];
        sum += texc * uKernel[i];
    }
    sum += COLOR_ADJUST;

    gl_FragColor = sum;
}
