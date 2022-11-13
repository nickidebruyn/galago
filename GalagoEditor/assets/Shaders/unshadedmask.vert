uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;

#if defined(HAS_TEXTURE)
    #define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;  //TEXTURE SEND INTO SHADER

varying vec2 vUv;   //TEXTURE SEND ON TO FRAGMENT SHADER

void main() {
    
    vUv = inTexCoord;
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}