/*
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
*/

#if defined(asdqwd)
vec4 getDirectionalLightColor(const in DirectionalLight light, in vec3 normal)
{
  float diffuse = max(0.0, dot(normalize(normal), -light.direction));
  return vec4(light.color * min(diffuse, 1.0), 1.0);
}

vec4 getPointLightColor(const in PointLight light, in vec3 worldPos, in vec3 normal)
{
  vec3 dif = worldPos - light.position;
  float dist = length(dif);
  normal = normalize(normal);

  float diffuse = max(0.0, dot(normal, -normalize(dif)));

  float difInf = 1.0;//-min(dist / length(light.range), 1.0);

  //float totalAtt = light.constAtt + light.linearAtt*dist + light.linearAtt*(dist*dist);
  return vec4(light.color * diffuse, 1.0) /** ((light.intensity+diffuse) / totalAtt)*/ * difInf;
}
#endif

//uniform int u_numDirLights;
//uniform int u_numPointLights;
uniform vec4 u_diffuseColor;
uniform vec3 u_ambientLight;

#if defined(numDirectionalLights)
struct DirectionalLight
{
  vec3 color;
  vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
#endif

#if defined(numPointLights)
struct PointLight
{
  vec3 color;
  vec3 position;
};
uniform PointLight u_pointLights[numPointLights];
#endif

varying vec4 v_color;
varying vec3 v_worldPos;

#if defined(normalFlag)
varying vec3 v_normal;
#endif

void main()
{
  vec4 color = v_color * u_diffuseColor;

#if defined(ambientLightFlag)
  vec3 light = vec4(u_ambientLight, 1.0);
#else
  vec3 light = vec4(0.5, 0.5, 0.5, 1.0);
#endif

#if defined(normalFlag)
  for (int i = 0; i < numDirectionalLights; i++) {
    float diffuse = max(0.0, dot(normalize(v_normal), -u_dirLights[i].direction));
    light += u_dirLights[i].color * min(diffuse, 1.0);
  }

  for (int i = 0; i < numPointLights; i++) {
    vec3 dif = v_worldPos - u_pointLights[i].position;
    float dist = length(dif);
    float diffuse = max(0.0, dot(v_normal, -normalize(dif)));
    float difInf = 1.0 - min(dist / length(vec3(3, 3, 3)), 1.0);
    float att = 0.7 + (0.1 * dist) + (0.00008 * dist * dist);

    light += u_pointLights[i].color * (diffuse/att) * difInf;
  }
#endif

  gl_FragColor = color * vec4(light, 1.0);
}