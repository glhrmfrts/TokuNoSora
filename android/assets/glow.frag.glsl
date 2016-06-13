#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;

varying vec2 v_texCoord0;

void main()
{
	vec4 dst = texture2D(u_sampler0, v_texCoord0);
	vec4 src = texture2D(u_sampler1, v_texCoord0);

	gl_FragColor = clamp((src + dst) - (src * dst), 0.0, 1.0);
	gl_FragColor.w = 1.0;
}