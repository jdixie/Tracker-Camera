#extension GL_OES_EGL_image_external : require
varying mediump vec2 textureCoordinate;
precision mediump float;


uniform samplerExternalOES videoFrame;
uniform vec4 inputColor;
uniform float threshold;

float abs(float a, float b)
{
	float val = a - b;

	if(val < 0.0)
		val = -1.0 * val;

	return val;
}

void main()
{
	float d;
	vec4 pixelColor;

	pixelColor = texture2D(videoFrame, textureCoordinate);
	float gray = (pixelColor.r + pixelColor.g + pixelColor.b) / 3.0;
	//d = distance(pixelColor.rgb, inputColor.rgb);

	vec4 finalColor = pixelColor;

	if(abs(pixelColor.r, inputColor.r) > threshold || abs(pixelColor.b, inputColor.b) > threshold || abs(pixelColor.g, inputColor.g) > threshold)
		finalColor = vec4(gray, gray, gray, pixelColor.a);

	//gl_FragColor = (d > threshold) ? pixelColor : vec4(gray, gray, gray, pixelColor.a);
	gl_FragColor = finalColor;
}