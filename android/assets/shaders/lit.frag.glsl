#define NUM_POINT_LIGHTS 64
#define NUM_DIRECTIONAL_LIGHTS 1

precision mediump float;

struct DirectionalLight
{
  vec3 dir;
  vec3 color;
  float intensity;
};

struct PointLight
{
  vec3 color;
  vec3 pos;
  vec3 range;
  float intensity;
  float constAtt;
  float linearAtt;
  float expAtt;
};

vec4 getDirectionalLightColor(const in DirectionalLight light, in vec3 normal)
{
  float diffuse = max(0.0, dot(normalize(normal), -light.dir));
  return vec4(light.color * min(light.intensity+diffuse, 1.0), 1.0);
}

vec4 getPointLightColor(const in PointLight light, in vec3 worldPos, in vec3 normal)
{
  vec3 dif = worldPos - light.pos;
  float dist = length(dif);
  normal = normalize(normal);

  float diffuse = max(0.0, dot(normal, -normalize(dif)));
  if (light.isShip && normal.z != 0)
    diffuse *= max(0.0, normal.z * -1);

  float difInf = 1.0-min(dist / length(light.range), 1.0);

  float totalAtt = light.constAtt + light.linearAtt*dist + light.linearAtt*(dist*dist);
  return vec4(light.color, 1.0) * ((light.intensity+diffuse) / totalAtt) * difInf;
}

uniform vec4 u_diffuseColor;
uniform vec3 u_ambientLight;
uniform DirectionalLight u_dirLights[NUM_DIRECTIONAL_LIGHTS];
uniform PointLight u_pointLights[NUM_POINT_LIGHTS];

varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_worldPos;

void main()
{
  vec4 light = u_ambientLight;
  for (int i = 0; i < NUM_DIRECTIONAL_LIGHTS; i++) {
      light += getDirectionalLightColor(sunLight, v_normal);
  }

  for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
      light += getPointLightColor(u_pointLights[i], v_worldPos, v_normal);
  }

  gl_FragColor = v_color * u_diffuseColor * light;
}