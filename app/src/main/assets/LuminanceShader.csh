#version 430
layout (local_size_x = 1, local_size_y = 1) in;

layout(std430, binding = 0) buffer SSBO { int luminance[]; };

//one of these will work
layout(r32f /* Format, must match parameter in glBindImageTexture() */, binding = 1) writeonly uniform highp image2D videoFrame;
//uniform samplerExternalOES videoFrame;

//determine inverse gamma value of an individual sRGB color (r, g, or b) to make it standard color
int invGamma(float val){
    if(val <= 0.04045)
        return val / 12.92;
    else
        return pow(((val + 0.055) / (1.055)), 2.4);
}

//take a standard color (r, g, or b) and apply gamma effect to make it sRGB compliant
int gamma(float val) {
    if(val <= 0.0031308)
        val *= 12.92;
    else
        val = 1.055*pow(val, 1.0 / 2.4) - 0.055;
    return int(val * 255+.5);
}

void main(){
    // get index in global work group i.e x,y position
    ivec2 pixel_coords = ivec2 (gl_GlobalInvocationID.xy);

    //grab color of pixel
    vec4 pixelColor = texture2D(videoFrame, textureCoordinate);

    //get luminance value of the pixel by undoing sRGB gamma, applying luminance formula for standard color, then turn it back into sRGB
    int lumVal = gamma(0.212655 * invGamma(pixelColor.r) + 0.715158 * invGamma(pixelColor.g) + 0.072187 * invGamma(pixelColor.b));

    //increment the array position for this luminance value
    atomicAdd(luminance[lumVal], 1);

    //wait for all workgroups to complete - do this in java code instead?
    //memoryBarrierShared();
    barrier();

}