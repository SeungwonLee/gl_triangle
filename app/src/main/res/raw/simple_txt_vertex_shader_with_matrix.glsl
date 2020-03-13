uniform mat4 uMVPMatrix;
uniform mat4 uTextureMatrix;
uniform mat4 uProjectionMatrix;

attribute vec4 vPosition;
attribute vec4 a_texCoord;
varying vec2 v_texCoord;

void main()
{
    v_texCoord = (uTextureMatrix * a_texCoord).xy;

    vec4 temp_v = uMVPMatrix * vPosition;
    gl_Position = uProjectionMatrix * temp_v;
}
