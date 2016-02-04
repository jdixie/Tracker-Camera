varying highp vec2 textureCoordinate;

uniform sampler2D videoFrame;

void main()
{
	gl_FragColor = vec4(1.0, 1.0, 0.0, 0.0); //texture2D(videoFrame, textureCoordinate);
}
