#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

#ifdef VERTEX_LIGHTING
    #import "Common/ShaderLib/BlinnPhongLighting.glsllib"    
#endif

// fog - jayfella
#ifdef USE_FOG
varying float fog_distance;
uniform vec3 g_CameraPosition;
#endif

uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

varying vec2 texCoord;
#ifdef SEPARATE_TEXCOORD
  varying vec2 texCoord2;
  attribute vec2 inTexCoord2;
#endif

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec3 lightVec;

#ifdef VERTEX_COLOR
  attribute vec4 inColor;
#endif

#ifndef VERTEX_LIGHTING
  attribute vec4 inTangent;

  #ifndef NORMALMAP
    varying vec3 vNormal;
  #endif  
  varying vec3 vViewDir;
  varying vec4 vLightDir;
#else
  varying vec2 vertexLightValues;
  uniform vec4 g_LightDirection;
#endif

#if (defined(PARALLAXMAP) || (defined(NORMALMAP_PARALLAX) && defined(NORMALMAP))) && !defined(VERTEX_LIGHTING) 
    varying vec3 vViewDirPrlx;
#endif

#ifdef USE_REFLECTION
    uniform vec3 g_CameraPosition;

    uniform vec3 m_FresnelParams;
    varying vec4 refVec;

    /**
     * Input:
     * attribute inPosition
     * attribute inNormal
     * uniform g_WorldMatrix
     * uniform g_CameraPosition
     *
     * Output:
     * varying refVec
     */
    void computeRef(in vec4 modelSpacePos){
        // vec3 worldPos = (g_WorldMatrix * modelSpacePos).xyz;
        vec3 worldPos = TransformWorld(modelSpacePos).xyz;

        vec3 I = normalize( g_CameraPosition - worldPos  ).xyz;
        // vec3 N = normalize( (g_WorldMatrix * vec4(inNormal, 0.0)).xyz );
        vec3 N = normalize( TransformWorld(vec4(inNormal, 0.0)).xyz );

        refVec.xyz = reflect(I, N);
        refVec.w   = m_FresnelParams.x + m_FresnelParams.y * pow(1.0 + dot(I, N), m_FresnelParams.z);
    }
#endif

//Wind/grass specific parameters
uniform float g_Time;
uniform float m_WindStrength;
uniform vec2 m_WindDirection;
uniform sampler2D m_Noise;
uniform float m_MoveFactor;

void main(){
   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;
   
   vec3 displacedVertex;
   displacedVertex = inPosition;
   
   float len = length( displacedVertex );
   float noiseCoord = g_Time;
   int totalTime = int(g_Time);
   if (totalTime > 4096) totalTime -= 4096;
   
   int pixelY = int(totalTime/64);
   int pixelX = totalTime/ -pixelY;
   float noiseFactor = texture2D(m_Noise, vec2( pixelX*10, pixelY*10 ) ).r;
   
   vec3 wvPosition = (TransformWorldView(modelSpacePos) * vec4(displacedVertex, 1.0)).xyz;
   
   if(inPosition.y>=0.1) {
       displacedVertex.x += m_MoveFactor * sin(g_Time * texture2D(m_Noise, wvPosition.xz*50.0).r + len) + (m_WindStrength * noiseFactor * m_WindDirection.x)/10.0;
       displacedVertex.z += m_MoveFactor * cos(g_Time * texture2D(m_Noise, wvPosition.zx*50.0).r + len) + (m_WindStrength * noiseFactor * m_WindDirection.y)/10.0;
   }   
   
   
   #ifndef VERTEX_LIGHTING
        vec3 modelSpaceTan  = inTangent.xyz;
   #endif

   #ifdef NUM_MORPH_TARGETS
        #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
           Morph_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
           Morph_Compute(modelSpacePos, modelSpaceNorm);
        #endif
   #endif

   #ifdef NUM_BONES
        #ifndef VERTEX_LIGHTING
        Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
        Skinning_Compute(modelSpacePos, modelSpaceNorm);
        #endif
   #endif

   //gl_Position = TransformWorldViewProjection(modelSpacePos);// g_WorldViewProjectionMatrix * modelSpacePos;
   gl_Position = g_WorldViewProjectionMatrix * vec4(displacedVertex, 1.0);
   
   texCoord = inTexCoord;
   #ifdef SEPARATE_TEXCOORD
      texCoord2 = inTexCoord2;
   #endif

   wvPosition = TransformWorldView(modelSpacePos).xyz;// (g_WorldViewMatrix * modelSpacePos).xyz;
   vec3 wvNormal  = normalize(TransformNormal(modelSpaceNorm));//normalize(g_NormalMatrix * modelSpaceNorm);
   vec3 viewDir = normalize(-wvPosition);
  
   vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
   wvLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;

   #if (defined(NORMALMAP) || defined(PARALLAXMAP)) && !defined(VERTEX_LIGHTING)
     vec3 wvTangent = normalize(TransformNormal(modelSpaceTan));
     vec3 wvBinormal = cross(wvNormal, wvTangent);
     mat3 tbnMat = mat3(wvTangent, wvBinormal * inTangent.w,wvNormal);
   #endif
 
   #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
     vViewDir  = -wvPosition * tbnMat;    
     #if (defined(PARALLAXMAP) || (defined(NORMALMAP_PARALLAX) && defined(NORMALMAP))) 
         vViewDirPrlx = vViewDir;
     #endif
     lightComputeDir(wvPosition, lightColor.w, wvLightPos, vLightDir, lightVec);
     vLightDir.xyz = (vLightDir.xyz * tbnMat).xyz;
   #elif !defined(VERTEX_LIGHTING)
     vNormal = wvNormal;
     vViewDir = viewDir;
     #if defined(PARALLAXMAP)
        vViewDirPrlx  =  -wvPosition * tbnMat;
     #endif
     lightComputeDir(wvPosition, lightColor.w, wvLightPos, vLightDir, lightVec);
   #endif

   #ifdef MATERIAL_COLORS
      AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
      DiffuseSum  =  m_Diffuse  * vec4(lightColor.rgb, 1.0);
      SpecularSum = (m_Specular * lightColor).rgb;
    #else
      // Defaults: Ambient and diffuse are white, specular is black.
      AmbientSum  = g_AmbientLightColor.rgb;
      DiffuseSum  =  vec4(lightColor.rgb, 1.0);
      SpecularSum = vec3(0.0);
    #endif

    #ifdef VERTEX_COLOR
      AmbientSum *= inColor.rgb;
      DiffuseSum *= inColor;
    #endif

    #ifdef VERTEX_LIGHTING
        float spotFallOff = 1.0;
        vec4 vLightDir;
        lightComputeDir(wvPosition, lightColor.w, wvLightPos, vLightDir, lightVec);
        #if __VERSION__ >= 110
            // allow use of control flow
        if(lightColor.w > 1.0){
        #endif           
           spotFallOff = computeSpotFalloff(g_LightDirection, lightVec);
        #if __VERSION__ >= 110           
        }
        #endif
        
        vertexLightValues = computeLighting(wvNormal, viewDir, vLightDir.xyz, vLightDir.w * spotFallOff, m_Shininess);
    #endif

    #ifdef USE_REFLECTION 
        computeRef(modelSpacePos);
    #endif

    #ifdef USE_FOG
    fog_distance = distance(g_CameraPosition, (TransformWorld(modelSpacePos)).xyz);
    #endif
}