uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_normalTrans;

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;

varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_worldPos;

void main()
{
  vec4 worldPos = u_worldTrans * vec4(vert, 1.0);
  gl_Position = u_projViewTrans * worldPos;

  vec4 correctNormal = u_normalTrans * vec4(a_normal, 0.0);

  v_color = a_color;
  v_normal = correctNormal.xyz;
  v_worldPos = worldPos.xyz;
}