#import "Common/ShaderLib/Skinning.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;

attribute vec2 inTexCoord;

varying vec2 texCoord;
varying float fog_z;

void main(){

    texCoord = inTexCoord;
    vec4 pos = vec4(inPosition, 1.0);

    #ifdef NUM_BONES
      Skinning_Compute(pos);
    #endif

    gl_Position = g_WorldViewProjectionMatrix * pos;

    fog_z = gl_Position.z;
}