#import "Common/ShaderLib/Skinning.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform float g_Time;

attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

const float pi = 3.14159;

#if defined(DEFORMY_WAVE)
    #define HAS_DEFORMWAVE
#endif

uniform float m_SpeedY;
uniform float m_SizeY;
uniform float m_DepthY;
uniform int m_DirY;
uniform float m_RotationY;


#ifdef HAS_DEFORMWAVE
vec3 displaceWave(in vec3 pos) {
    vec3 new_pos = vec3(pos);

    #ifdef DEFORMY_WAVE
        float speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
        float dist1Y = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        float dist2Y = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.x > 0.0) dist1Y = -dist1Y;
        if (new_pos.z > 0.0) dist2Y = -dist2Y;
        float time1Y = ((g_Time*speedY)*sin(m_RotationY*(pi/180.0)));
        float time2Y = ((g_Time*speedY)*cos(m_RotationY*(pi/180.0)));
        float wave1Y = ( m_DepthY*( sin( (m_SizeY*dist1Y)+time1Y ) ) );
        float wave2Y = ( m_DepthY*( sin( (m_SizeY*dist2Y)+time2Y ) ) );
        new_pos.y += wave1Y;
        new_pos.y += wave2Y;
    #endif

    return new_pos;
}
#endif


void main(){
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    vec3 displacedVertex = vec3(inPosition);
     
    #ifdef HAS_DEFORMWAVE
        displacedVertex = displaceWave(displacedVertex);
    #endif

   gl_Position = g_WorldViewProjectionMatrix * vec4(displacedVertex, 1.0);      

}