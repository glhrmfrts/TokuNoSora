#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;
uniform float u_exposure;

varying vec2 v_texCoord0;

void main()
{
/*
	vec3 dst = texture2D(u_sampler0, v_texCoord0).rgb;
	vec3 src = texture2D(u_sampler1, v_texCoord0).rgb;

	gl_FragColor = vec4((src + dst) - (src * dst), 1.0);
*/
        float gamma = 1.0;
	vec3 dst = texture2D(u_sampler0, v_texCoord0).rgb;
	vec3 src = texture2D(u_sampler1, v_texCoord0).rgb;
	vec3 hdr = (src + dst) - (src * dst);
	hdr = vec3(1.0) - exp(-hdr * u_exposure);
	hdr = pow(hdr, vec3(1.0 / gamma));

	gl_FragColor = vec4(hdr, 1.0);
}