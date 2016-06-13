uniform vec4 u_diffuseColor;

varying vec4 v_color;

void main()
{
    gl_FragColor = v_color * u_diffuseColor;
}