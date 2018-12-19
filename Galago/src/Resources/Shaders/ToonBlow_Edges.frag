#ifdef TOON_EDGES
uniform vec4 m_EdgesColor;
vec4 toonEdges;
#endif

#ifdef FOG_EDGES
  #ifdef FOG
    varying float fog_z;
    uniform vec4 m_FogColor;
    vec4 fogColor;
    float fogFactor;
  #endif

  #ifdef FOG_SKY
#import "Common/ShaderLib/Optics.glsllib"
    uniform ENVMAP m_FogSkyBox;
    varying vec3 I;
  #endif
#endif

void main(){

#ifdef TOON_EDGES
gl_FragColor = m_EdgesColor;
#else
gl_FragColor = vec4(0.0);
#endif


#ifdef FOG_EDGES
   #ifdef FOG

fogColor = m_FogColor;

    #ifdef FOG_SKY
fogColor.rgb = Optics_GetEnvColor(m_FogSkyBox, I).rgb;
    #endif

float fogDistance = fogColor.a;
float depth = (fog_z - fogDistance)/ fogDistance;
depth = max(depth, 0.0);
fogFactor = exp2(-depth*depth);
fogFactor = clamp(fogFactor, 0.05, 1.0);

gl_FragColor.rgb = mix(fogColor.rgb,gl_FragColor.rgb,vec3(fogFactor));

   #endif
#endif

}
