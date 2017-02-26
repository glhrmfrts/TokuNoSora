#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;
uniform float u_exposure;

varying vec2 v_texCoord0;

void main()
{
	vec3 dst = texture2D(u_sampler0, v_texCoord0).rgb;
	vec3 src = texture2D(u_sampler1, v_texCoord0).rgb;

	gl_FragColor = vec4((src + dst) - (src * dst), 1.0);
}