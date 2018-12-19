#import "Common/ShaderLib/Skinning.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;


varying vec2 texCoord;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

attribute vec4 inTangent;
varying vec3 mat;
  
varying vec3 vNormal;


#if defined(NORMALMAP)
    uniform vec3 g_CameraPosition;
    uniform mat4 g_WorldMatrix;
#endif 

//varying vec3 vPosition;
//varying vec3 vViewDir;

#ifdef FOG
    varying float fog_z;
#endif

#if defined(FOG_SKY)
    varying vec3 I;
    
    #ifndef NORMALMAP
        uniform vec3 g_CameraPosition;
        uniform mat4 g_WorldMatrix;
    #endif
#endif 

void main() {

   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;
   vec3 modelSpaceTan  = inTangent.xyz;

    #ifdef NUM_BONES
      Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
    #endif

    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
    texCoord = inTexCoord;

    vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
    vec3 wvNormal  = normalize(g_NormalMatrix * modelSpaceNorm);
    vec3 viewDir = normalize(-wvPosition);

    #ifdef NORMALMAP
        vec3 wvTangent = normalize(g_NormalMatrix * modelSpaceTan);
        vec3 wvBinormal = cross(wvNormal, wvTangent);

        mat3 tbnMat = mat3(wvTangent, wvBinormal * inTangent.w, wvNormal);
        mat = vec3(1.0) * tbnMat;
        mat = normalize(mat);
        //vPosition = wvPosition * tbnMat;
        //vViewDir  = viewDir * tbnMat;
        //vViewDir  = -wvPosition * tbnMat;

    #else
     
        //vPosition = wvPosition;
        //vViewDir = viewDir;

    #endif

    vNormal = wvNormal;

    #if defined(FOG_SKY)
        vec3 worldPos = (g_WorldMatrix * modelSpacePos).xyz;
        I = normalize(g_CameraPosition - worldPos).xyz;
    #endif

    #ifdef FOG
        fog_z = gl_Position.z;
    #endif

}