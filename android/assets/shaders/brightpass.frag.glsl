#ifdef GL_ES
  precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform float u_threshold;

varying vec2 v_texCoord0;

void main()
{
  // TODO: use threshold
  vec3 color = texture2D(u_sampler0, v_texCoord0).rgb;
  float brightness = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);

  if (brightness > u_threshold) {
    gl_FragColor = vec4(color, 1.0);
  } else {
    gl_FragColor = vec4(0);
  }
}