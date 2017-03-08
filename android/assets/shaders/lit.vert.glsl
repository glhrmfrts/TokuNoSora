uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec3 a_position;
attribute vec4 a_color;

varying vec4 v_color;
varying vec3 v_worldPos;

#if defined(normalFlag)
attribute vec3 a_normal;
uniform mat3 u_normalMatrix;
varying vec3 v_normal;
#endif

void main()
{
  vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);
  gl_Position = u_projViewTrans * worldPos;

  v_color = a_color;
  v_worldPos = worldPos.xyz;

#if defined(normalFlag)
  vec3 correctNormal = u_normalMatrix * a_normal;
  v_normal = correctNormal;
#endif
}