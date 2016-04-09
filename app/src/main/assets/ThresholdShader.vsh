attribute vec4 position;
attribute mediump vec4 inputTextureCoordinate;
uniform mat4 textureMatrix;

varying mediump vec2 textureCoordinate;

void main()
{
	gl_Position = position;
	textureCoordinate = (textureMatrix * inputTextureCoordinate).xy;
}