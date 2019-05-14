precision mediump float;

uniform sampler2D s_texture;
varying vec2 v_texCoord;

void main()
{
    vec4 tc = texture2D(s_texture, v_texCoord);
    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
    gl_FragColor = vec4(color, color, color, 1.0);
}
