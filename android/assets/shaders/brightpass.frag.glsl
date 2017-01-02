#ifdef GL_ES
  precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform float u_threshold;

varying vec2 v_texCoord0;

void main()
{
  // TODO: use threshold
  vec4 color = texture2D(u_sampler0, v_texCoord0);
  float brightness = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);
  gl_FragColor = color * brightness;
}