precision mediump float;

uniform sampler2D uTexture;
varying vec2 v_texCoord;
//uniform float uScale;// tile size
//uniform vec2 uTexel;// texel size

float filterSize = 25.0;

void main() {
    // float dx = uScale * uTexel.x;
    // float dy = uScale * uTexel.y;
    // vec2 coord = vec2(floor(vTexCoord.x/dx) * dx + (dx / 2.0), floor(vTexCoord.y/dy) * dy + (dy / 2.0));
     vec2 coord = floor(v_texCoord.xy * filterSize) / filterSize;
    gl_FragColor = texture2D(uTexture, coord);
}
