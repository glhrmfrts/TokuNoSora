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
  float intensity;
};
uniform PointLight u_pointLights[numPointLights];
#endif

#if defined(colorFlag)
varying vec4 v_color;
#endif

varying vec3 v_worldPos;

#if defined(normalFlag)
varying vec3 v_normal;
#endif

void main()
{
#if defined(colorFlag)
  vec4 color = v_color * u_diffuseColor;
#else
  vec4 color = u_diffuseColor;
#endif

#if defined(ambientLightFlag)
  vec3 light = u_ambientLight;
#else
  vec3 light = vec3(0.5, 0.5, 0.5);
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

    light += u_pointLights[i].color * (diffuse/att) * difInf * u_pointLights[i].intensity;
  }
#endif

  gl_FragColor = color * vec4(light, 1.0);
}