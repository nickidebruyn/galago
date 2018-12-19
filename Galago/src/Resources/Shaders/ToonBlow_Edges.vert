#import "Common/ShaderLib/Skinning.glsllib"
#define ATTENUATION

uniform float m_EdgeSize; 

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

#ifdef FOG_EDGES
#ifdef FOG
    varying float fog_z;
#endif

#if defined(FOG_SKY)
    varying vec3 I;
    uniform vec3 g_CameraPosition;
    uniform mat4 g_WorldMatrix;
#endif 
#endif 


void main(){



    if (m_EdgeSize != 0.0) {
   vec4 pos = vec4(inPosition, 1.0);
   vec4 normal = vec4(inNormal,0.0);

   normal = normalize(normal);
   pos = pos + normal * m_EdgeSize;

    #ifdef NUM_BONES
      Skinning_Compute(pos);
    #endif

   gl_Position = g_WorldViewProjectionMatrix * pos;


#ifdef FOG_EDGES
  #if defined(FOG_SKY)
       vec3 worldPos = (g_WorldMatrix * pos).xyz;
       I = normalize( g_CameraPosition -  worldPos  ).xyz;
  #endif

   #ifdef FOG
    fog_z = gl_Position.z;
   #endif
#endif


   } else {
     gl_Position = vec4(1000.0,1000.0,1000.0,1000.0);
   }



}

