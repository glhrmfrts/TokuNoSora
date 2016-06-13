#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D u_sampler0;
uniform vec2 u_texelSize;
uniform int u_orientation;
uniform int u_blurAmount;
uniform float u_blurScale;
uniform float u_blurStrength;

varying vec2 v_texCoord0;

float gaussian(float x, float deviation)
{
	return (1.0 / sqrt(2.0 * 3.141592 * deviation)) * exp(-((x * x) / (2.0 * deviation)));
}

void main()
{
	float halfBlur = float(u_blurAmount) * 0.5;
	vec4 colour = vec4(0.0);
	vec4 texColour = vec4(0.0);

	// gaussian deviation
	float deviation = halfBlur * 0.35;
	deviation *= deviation;
	float strength = 1.0 - u_blurStrength;

	if (u_orientation == 0)
	{
		// horizontal blur
		for (int i = 0; i < 10; ++i)
		{
			if (i >= u_blurAmount)
				break;

			float offset = float(i) - halfBlur;
			texColour = texture2D(u_sampler0, v_texCoord0 + vec2(offset * u_texelSize.x * u_blurScale, 0.0)) * gaussian(offset * strength, deviation);
			colour += texColour;
		}
	}
	else
	{
		// vertical blur
		for (int i = 0; i < 10; ++i)
		{
			if (i >= u_blurAmount)
				break;

			float offset = float(i) - halfBlur;
			texColour = texture2D(u_sampler0, v_texCoord0 + vec2(0.0, offset * u_texelSize.y * u_blurScale)) * gaussian(offset * strength, deviation);
			colour += texColour;
		}
	}

	// apply colour
	gl_FragColor = clamp(colour, 0.0, 1.0);
	gl_FragColor.w = 1.0;
}