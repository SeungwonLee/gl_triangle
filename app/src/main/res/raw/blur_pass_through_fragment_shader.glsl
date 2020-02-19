precision mediump float;

uniform sampler2D image;

out vec4 FragmentColor;

void main(void)
{
    FragmentColor = texture2D(image, vec2(gl_FragCoord)/1024.0);
}