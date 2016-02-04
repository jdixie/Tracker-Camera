attribute vec4 position;
attribute vec4 inputTextureCoordinate;
uniform mat4 textureMatrix;

varying vec2 textureCoordinate;

void main()
{
    gl_Position = position;
    textureCoordinate = (textureMatrix * inputTextureCoordinate).xy;
}