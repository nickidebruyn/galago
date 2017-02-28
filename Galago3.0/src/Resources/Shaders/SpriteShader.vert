
uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;

#if defined(HAS_TEXTURE)
    #define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;
attribute vec4 inColor;

varying vec2 texCoord1;

varying vec4 vertColor;

void main(){
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}