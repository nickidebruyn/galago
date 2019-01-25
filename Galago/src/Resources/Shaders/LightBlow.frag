#define ATTENUATION
//#define HQ_ATTENUATION

varying vec2 texCoord;
    #ifdef SEPERATE_TEXCOORD
        varying vec2 texCoord2;
    #endif
    #ifdef SEPERATE_TEXCOORD2
        varying vec2 texCoord3;
    #endif


varying vec3 AmbientSum;
varying vec4 DiffuseSum;

#if defined(SPECULAR_LIGHTING)
varying vec3 SpecularSum;
uniform float m_Shininess;
#endif

#ifdef MULTIPLY_COLOR
uniform vec4 m_Diffuse;
#endif

#ifdef HAS_LIGHTMAP
    uniform sampler2D m_LightMap;
#endif

#ifdef VERTEX_COLOR
  varying vec4 vColor;
#endif

  uniform vec4 g_LightDirection;
//  varying vec3 vPosition;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
  varying vec3 mat;
  varying vec3 lightVec;


#ifdef SPECULARMAP
  uniform sampler2D m_SpecularMap;
#endif

#if defined(PARALLAXMAP) || defined(NORMALMAP_PARALLAX) && defined(NORMALMAP)
  #import "Common/ShaderLib/Parallax.glsllib"
  uniform sampler2D m_ParallaxMap;
  float m_ParallaxHeight;
#endif


#ifdef DIFFUSEMAP
  uniform sampler2D m_DiffuseMap;
  
#endif
#if defined(DIFFUSEMAP_1)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_DiffuseMap_1;
    #endif
#endif
#if defined(DIFFUSEMAP_2)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_DiffuseMap_2;
    #endif
#endif
#if defined(DIFFUSEMAP_3)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_DiffuseMap_3;
    #endif
#endif

  
#ifdef NORMALMAP
  uniform sampler2D m_NormalMap;
  vec4 normalHeight;
#else
  varying vec3 vNormal;
#endif


#if defined(NORMALMAP_1) 
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_NormalMap_1;
    #endif
#endif
#if defined(NORMALMAP_2) 
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_NormalMap_2;
    #endif
#endif
#if defined(NORMALMAP_3) 
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
     uniform sampler2D m_NormalMap_3;
    #endif
#endif


#if defined(NORMALMAP) || defined(DIFFUSEMAP)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
      uniform float m_uv_0_scale;  
    #endif
#endif
#if defined(NORMALMAP_1) || defined(DIFFUSEMAP_1)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
  uniform float m_uv_1_scale;  
    #endif
#endif
#if defined(NORMALMAP_2) || defined(DIFFUSEMAP_2)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
  uniform float m_uv_2_scale;  
    #endif
#endif
#if defined(NORMALMAP_3) || defined(DIFFUSEMAP_3)
    #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
  uniform float m_uv_3_scale;  
    #endif
#endif


#ifdef TEXTURE_MASK
  uniform sampler2D m_TextureMask;
#endif


#ifdef ALPHAMAP
  uniform sampler2D m_AlphaMap;
#endif


#ifdef COLORRAMP
  uniform sampler2D m_ColorRamp;
#endif

 uniform float m_AlphaDiscardThreshold;

#ifdef HQ_ATTENUATION
uniform vec4 g_LightPosition;
#endif

#if defined (RIM_LIGHTING) || defined (RIM_LIGHTING_2)
uniform vec4 m_RimLighting;
uniform vec4 m_RimLighting2;
// uniform vec4 g_AmbientLightColor;
#endif

#if defined(IBL) || defined(REFLECTION) || defined(FOG_SKY)
#import "Common/ShaderLib/Optics.glsllib"
#endif

#if defined(IBL) || defined(IBL_SIMPLE) || defined(REFLECTION)
varying vec3 refVec;
#endif

#ifdef IBL 
uniform ENVMAP m_IblMap;
#endif

#ifdef IBL_SIMPLE 
uniform sampler2D m_IblMap_Simple;
#endif

#ifdef REFLECTION 
    uniform float m_RefIntensity;
//    varying vec3 refVec;
    uniform ENVMAP m_RefMap;
    #endif


#ifdef MINNAERT
uniform vec4 m_Minnaert;
#endif


#ifdef FOG
    varying float fog_z;
    uniform vec4 m_FogColor;
    vec4 fogColor;
    float fogFactor;
#endif
  #ifdef FOG_SKY
    uniform ENVMAP m_FogSkyBox;
    varying vec3 I;
  #endif


float overlayMode(float color, float overlayColor)
{
        float result;
            if (color < 0.5) {
                   result = 2.0 * color * overlayColor;
                } else {
               result = 1.0 - 2.0 * (1.0 - overlayColor) * (1.0 - color);
            }
		return result;
}

float srgb_to_linearrgb(float c)
{
	if(c < 0.04045)
		return (c < 0.0)? 0.0: c * (1.0/12.92);
	else
		return pow((c + 0.055)*(1.0/1.055), 2.4);
}

float linearrgb_to_srgb(float c)
{
	if(c < 0.0031308)
		return (c < 0.0)? 0.0: c * 12.92;
	else
		return 1.055 * pow(c, 1.0/2.4) - 0.055;
}

void srgb_to_linearrgb(vec4 col_from, out vec4 col_to)
{
	col_to.r =  srgb_to_linearrgb(col_from.r);
	col_to.g = srgb_to_linearrgb(col_from.g);
	col_to.b = srgb_to_linearrgb(col_from.b);
	col_to.a = col_from.a;
}

void linearrgb_to_srgb(vec4 col_from, out vec4 col_to)
{
	col_to.r = linearrgb_to_srgb(col_from.r);
	col_to.g = linearrgb_to_srgb(col_from.g);
	col_to.b = linearrgb_to_srgb(col_from.b);
	col_to.a = col_from.a;
}



float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
    #ifdef V_TANGENT
        d = 1.0 - d*d;
        return step(0.0, d) * sqrt(d);
    #else
        return d;
    #endif
}


float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
 
     #if defined(HEMI_LIGHTING_1)
       return (0.5 + 0.5 * dot(norm, lightdir)) * (0.5 + 0.5 * dot(norm, lightdir));
    #elif !defined(HEMI_LIGHTING_1) && defined(HEMI_LIGHTING_2)
       return 0.4 + 0.5 * dot(norm, lightdir);
    #else

       #if defined(LINEAR_LIGHTING)
           return max(0.0, dot(norm, lightdir));
       #else
           return linearrgb_to_srgb(max(0.0, dot(norm, lightdir)));
       #endif

    #endif
}


#if defined(SPECULAR_LIGHTING)
float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
    // NOTE: check for shiny <= 1 removed since shininess is now 
    // 1.0 by default (uses matdefs default vals)
    #ifdef LOW_QUALITY
       // Blinn-Phong
       // Note: preferably, H should be computed in the vertex shader
       
       vec3 H = (viewdir + lightdir) * vec3(0.5);
       return pow(max(tangDot(H, norm), 0.0), shiny);

    #elif defined(WARDISO)
        // Isotropic Ward
        vec3 halfVec = normalize(viewdir + lightdir);
        float NdotH  = max(0.001, tangDot(norm, halfVec));
        float NdotV  = max(0.001, tangDot(norm, viewdir));
        float NdotL  = max(0.001, tangDot(norm, lightdir));
        float a      = tan(acos(NdotH));
        float p      = max(shiny/128.0, 0.001);
        return NdotL * (1.0 / (4.0*3.14159265*p*p)) * (exp(-(a*a)/(p*p)) / (sqrt(NdotV * NdotL)));
    #else
       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);
    #endif
}
#endif


vec2 computeLighting(in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
   
float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
float specularFactor;
    
    #ifdef SPECULAR_LIGHTING
    specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);
 //   specularFactor =  (specularFactor * step(1.0, m_Shininess));

if (m_Shininess <= 1.0) {
specularFactor = 0.0; // should be one instruction on most cards ..
}

     #else
   specularFactor = 0.0;
     #endif

   #if defined (HQ_ATTENUATION)
    float att = clamp(1.0 - g_LightPosition.w * length(lightVec), 0.0, 1.0);
    #elif defined (NO_ATTENUATION)
    float att = 1.0;
   #else
    float att = vLightDir.w;
   #endif



return vec2(diffuseFactor, specularFactor) * vec2(att);
}




void main(){
    
  vec2 newTexCoord = texCoord;

       // Workaround, since it is not possible to modify varying variables
       #if defined(SPECULAR_LIGHTING)
       vec4 SpecularSum2 = vec4(SpecularSum, 1.0);
       #endif
       vec3 AmbientSum2 = AmbientSum;


#if defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
vec4 textureBlend;
   #if defined(TEXTURE_MASK)
       #ifdef SEPERATE_TEXCOORD2
        textureBlend = texture2D( m_TextureMask, texCoord2.xy);
            #else
        textureBlend = texture2D( m_TextureMask, texCoord.xy);
       #endif
   #elif defined(VERTEX_COLOR)    
       textureBlend = vColor;
   #endif
#endif


#if defined(NORMALMAP)
vec4 normalHeightCalc;

    #if  !defined(TEXTURE_MASK) && !defined(VERTEX_COLOR)
    normalHeightCalc = texture2D(m_NormalMap, newTexCoord);
    #elif  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
    normalHeightCalc = texture2D(m_NormalMap, newTexCoord* m_uv_0_scale);
    #endif

#endif

    #if defined(NORMALMAP_1)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
        vec4 normalHeight1 = texture2D(m_NormalMap_1, newTexCoord * m_uv_1_scale);
        normalHeightCalc.rgb = mix( normalHeightCalc.rgb, normalHeight1.rgb, textureBlend.r ).rgb;
        #endif
    #endif
    #if defined(NORMALMAP_2)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
        vec4 normalHeight2 = texture2D(m_NormalMap_2, newTexCoord * m_uv_2_scale);
        normalHeightCalc.rgb = mix( normalHeightCalc.rgb, normalHeight2.rgb, textureBlend.g ).rgb;
        #endif
    #endif
    #if defined(NORMALMAP_3)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
        vec4 normalHeight3 = texture2D(m_NormalMap_3, newTexCoord * m_uv_3_scale);
        normalHeightCalc.rgb = mix( normalHeightCalc.rgb, normalHeight3.rgb, textureBlend.b ).rgb;
        #endif
    #endif


   #if (defined(PARALLAXMAP) || (defined(NORMALMAP_PARALLAX) && defined(NORMALMAP)))

       #ifdef STEEP_PARALLAX
           #ifdef NORMALMAP_PARALLAX
               //parallax map is stored in the alpha channel of the normal map         
               newTexCoord = steepParallaxOffset(m_NormalMap, vViewDir, texCoord, m_ParallaxHeight);
           #else
               //parallax map is a texture
               newTexCoord = steepParallaxOffset(m_ParallaxMap, vViewDir, texCoord, m_ParallaxHeight);         
           #endif
       #else
           #ifdef NORMALMAP_PARALLAX
               //parallax map is stored in the alpha channel of the normal map         
               newTexCoord = classicParallaxOffset(m_NormalMap, vViewDir, texCoord, m_ParallaxHeight);
           #else
               //parallax map is a texture
               newTexCoord = classicParallaxOffset(m_ParallaxMap, vViewDir, texCoord, m_ParallaxHeight);
           #endif
       #endif
    #else
       newTexCoord = texCoord;    
    #endif


#ifdef DIFFUSEMAP

vec4 diffuseColor;

    #if !defined(TEXTURE_MASK) && !defined(VERTEX_COLOR)
      diffuseColor = texture2D(m_DiffuseMap, newTexCoord);
    #elif defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
      diffuseColor = texture2D(m_DiffuseMap, newTexCoord* m_uv_0_scale);
    #endif

    #if defined(DIFFUSEMAP_1)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
          vec4 diffuseColor1 = texture2D(m_DiffuseMap_1, newTexCoord * m_uv_1_scale);
          diffuseColor.rgb = mix( diffuseColor.rgb, diffuseColor1.rgb, textureBlend.r ).rgb;
        #endif  
    #endif  
      #if defined(DIFFUSEMAP_2)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
          vec4 diffuseColor2 = texture2D(m_DiffuseMap_2, newTexCoord * m_uv_2_scale);
          diffuseColor.rgb = mix( diffuseColor.rgb, diffuseColor2.rgb, textureBlend.g ).rgb;
        #endif  
      #endif  
        #if defined(DIFFUSEMAP_3)
            #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
             vec4 diffuseColor3 = texture2D(m_DiffuseMap_3, newTexCoord * m_uv_3_scale);
             diffuseColor.rgb = mix( diffuseColor.rgb, diffuseColor3.rgb, textureBlend.b ).rgb;
             #endif  
        #endif  
#else
     vec4 diffuseColor = vec4(0.6, 0.6, 0.6, 1.0);
#endif


    #if defined(NORMALMAP)
      normalHeight = normalHeightCalc;
      vec3 normal = normalize(normalHeight.xyz * vec3(2.0) - vec3(1.0));

       #ifdef LATC
         normal.z = sqrt(1.0 - (normal.x * normal.x) - (normal.y * normal.y));
       #endif

       #if defined (NOR_INV_X) && (NORMALMAP) 
         normal.x = -normal.x;
       #endif

       #if defined (NOR_INV_Y) && (NORMALMAP)
         normal.y = -normal.y;
       #endif

       #if defined (NOR_INV_Z) && (NORMALMAP)
        normal.z = -normal.z;
       #endif
 
    #else
        vec3 normal = vNormal;
   
        #if !defined(LOW_QUALITY) && !defined(V_TANGENT)
           normal = normalize(normal);
        #endif
    #endif


    #if defined(SPECULAR_LIGHTING) && defined(SPECULARMAP)
      vec4 specularColor;
        specularColor = texture2D(m_SpecularMap, newTexCoord);
    #else
      vec4 specularColor = vec4(1.0);
    #endif

    #if defined(SPECULAR_LIGHTING) && defined(SPEC_A_NOR) && defined(NORMALMAP) && !defined(SPECULARMAP)
        specularColor =  vec4(normalHeight.a);


    #elif defined(SPECULAR_LIGHTING) && defined(SPEC_A_DIF) && !defined(SPEC_A_NOR) && defined(DIFFUSEMAP) && !defined(SPECULARMAP)
          float specA = diffuseColor.a;

    #if defined(DIFFUSEMAP_1) && defined(SPEC_A_DIF)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
        diffuseColor1.a = diffuseColor1.a;
         specA = mix( specA, diffuseColor1.a, textureBlend.r );
        #endif
    #endif  
    #if defined(DIFFUSEMAP_2) && defined(SPEC_A_DIF)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
         diffuseColor2.a = diffuseColor2.a;
         specA = mix( specA, diffuseColor2.a, textureBlend.g );
        #endif
    #endif  
    #if defined(DIFFUSEMAP_3) && defined(SPEC_A_DIF)
        #if  defined(TEXTURE_MASK) || defined(VERTEX_COLOR)
        diffuseColor3.a = diffuseColor3.a;
         specA = mix( specA, diffuseColor3.a, textureBlend.b );
        #endif
    #endif  

 specularColor = vec4(specA); 
    #endif






     float alpha = DiffuseSum.a;

    #if defined (ALPHA_A_DIF) && defined (DIFFUSEMAP)
       alpha *=  diffuseColor.a;
    #elif defined (ALPHA_A_NOR) && defined (NORMALMAP)
       alpha *= normalHeight.a;   
    #endif

    if(alpha < m_AlphaDiscardThreshold){
        discard;
    }


        float spotFallOff = 1.0;

        #if __VERSION__ >= 110
          // allow use of control flow
          if(g_LightDirection.w != 0.0){
        #endif

          vec3 L       = normalize(lightVec.xyz);
          vec3 spotdir = normalize(g_LightDirection.xyz);
          float curAngleCos = dot(-L, spotdir);             
          float innerAngleCos = floor(g_LightDirection.w) * 0.001;
          float outerAngleCos = fract(g_LightDirection.w);
          float innerMinusOuter = innerAngleCos - outerAngleCos;
          spotFallOff = (curAngleCos - outerAngleCos) / innerMinusOuter;

          #if __VERSION__ >= 110
              if(spotFallOff <= 0.0){
                  gl_FragColor.rgb = AmbientSum2 * diffuseColor.rgb;
                  gl_FragColor.a   = alpha;
                  return;
              }else{
                  spotFallOff = clamp(spotFallOff, 0.0, 1.0);
              }
             }
          #else
             spotFallOff = clamp(spotFallOff, step(g_LightDirection.w, 0.001), 1.0);
          #endif




       vec4 lightDir = vLightDir;
       lightDir.xyz = normalize(lightDir.xyz);
       vec3 viewDir = normalize(vViewDir);

       vec2   light = computeLighting(normal, viewDir, lightDir.xyz) * spotFallOff;


      #ifdef MULTIPLY_COLOR
        diffuseColor.rgb *= m_Diffuse.rgb;
       #endif


       #ifdef COLORRAMP
           light.x = texture2D(m_ColorRamp, vec2(light.x, 0.0)).r;
           light.y = texture2D(m_ColorRamp, vec2(light.y, 0.0)).r;
       #endif




        #ifdef IBL_SIMPLE
       // IBL - Image Based Lighting. The lighting based on either cube map or sphere map.

           vec4 tempVec1;
           #if  defined (IBL_SIMPLE) && defined (NORMALMAP)
              vec3 iblLight = texture2D(m_IblMap_Simple, vec2((((refVec) + mat * normal) * vec3(0.49)) + vec3(0.49)));
           #elif  defined (IBL_SIMPLE) && !defined (NORMALMAP)
              vec3 iblLight = texture2D(m_IblMap_Simple,  vec2((refVec * vec3(0.49)) + vec3(0.49)));
           #endif
        
        //Albedo 
        AmbientSum2.rgb += iblLight;
        #endif

        #ifdef IBL
       // IBL - Image Based Lighting. The lighting based on either cube map or sphere map.
           vec4 tempVecIBL;
           
           #if  defined (IBL) && defined (NORMALMAP)
            vec4 iblLight = Optics_GetEnvColor(m_IblMap, (refVec + mat * normal));
           #elif  defined (IBL) && !defined (NORMALMAP)
            vec4 iblLight = Optics_GetEnvColor(m_IblMap,  refVec);
           #endif
        
          //Albedo 
          AmbientSum2.rgb += iblLight.rgb;
        #endif


#if defined(EMISSIVEMAP) && defined(DIFFUSEMAP)
        //Illumination based on diffuse map alpha chanel.
	float emissiveTex = diffuseColor.a;

//          AmbientSum2.rgb = AmbientSum2.rgb + 2.0 * emissiveTex;  
//        AmbientSum2.rgb = max(AmbientSum2.rgb, emissiveTex);
        light.x = light.x + emissiveTex;
   //     light.x = max(light.x,  emissiveTex);
        //diffuseColor.rgb = max(diffuseColor, emissiveTex); 

        #endif


#if defined (REFLECTION) 
    
    #if  defined (REFLECTION) && defined (NORMALMAP)
      vec4 refGet = Optics_GetEnvColor(m_RefMap, (refVec + (mat * normal)));
   #elif defined (REFLECTION) && !defined (NORMALMAP)
      vec4 refGet = Optics_GetEnvColor(m_RefMap, refVec);
    #endif

    vec3 refColor = refGet.rgb * m_RefIntensity;

    #if defined(REF_A_NOR) && defined(NORMALMAP)
    normalHeight.a = normalHeight.a;
    refColor.rgb *= vec3(normalHeight.a);
    #elif defined(REF_A_DIF)  && defined(DIFFUSEMAP)
    diffuseColor.a = diffuseColor.a;
    refColor.rgb *= vec3(diffuseColor.a);
    #endif
    
    
    

#ifdef ADDITIVE_REFLECTION
AmbientSum2.rgb +=  refColor*0.5;
diffuseColor.rgb += refColor;
//AmbientSum2.rgb = max(AmbientSum2.rgb, refGet* refTex);
#else
light.x = max(vec3(light.x), refColor);
 diffuseColor.rgb = max(diffuseColor.rgb, refColor);   
 #endif

#endif



#ifdef MINNAERT
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only

        vec3 minnaert = pow( 1.0 - dot( normal.xyz, viewDir ), 1.5 ) * m_Minnaert.xyz * m_Minnaert.w;
      //  minnaert.a = 0.0;
       AmbientSum2 += minnaert.rgb*light.x;
    //   light.x += minnaert*0.1;
// }
#endif

#ifdef RIM_LIGHTING
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only
        vec4 rim = pow( 1.0 - dot( normal, viewDir ), 1.5 ) * m_RimLighting * m_RimLighting.w;
        rim.a = 0.0;
       AmbientSum2 += rim.rgb*diffuseColor.rgb;
    //   light.x += rim*0.1;
// }
#endif

#ifdef RIM_LIGHTING_2
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only
        vec3 rim2 = pow( 1.0 - dot( normal, viewDir ), 1.5 ) * m_RimLighting2.xyz * m_RimLighting2.w;

        AmbientSum2 += rim2;
      //  rim2.a = 0.0;
     //  gl_FragColor.rgb += rim2.rgb*diffuseColor.rgb;
    //   light.x += rim2*0.1;
// }
#endif


 #ifdef HAS_LIGHTMAP

     vec4 lightMapColor;

           #if defined(SEPERATE_TEXCOORD) && !defined(SEPERATE_TEXCOORD2)
            lightMapColor = (texture2D(m_LightMap, texCoord2));
           #elif defined(SEPERATE_TEXCOORD) && defined(SEPERATE_TEXCOORD2)
            lightMapColor = (texture2D(m_LightMap, texCoord3));
        #else
            lightMapColor = (texture2D(m_LightMap, texCoord));
        #endif
    


    #if defined(LIGHTMAP_R)
                diffuseColor.rgb  *= vec3(lightMapColor.r);


        #elif defined(LIGHTMAP_G)
                diffuseColor.rgb  *= vec3(lightMapColor.g);

        #elif defined(LIGHTMAP_B)
                diffuseColor.rgb  *= vec3(lightMapColor.b);

        #elif defined(LIGHTMAP_A)
                diffuseColor.rgb  *= vec3(lightMapColor.a);

        #else
                diffuseColor.rgb  *= lightMapColor.rgb;
    #endif

     #ifdef SPECULAR_LIGHTING
        #if defined(LIGHTMAP_R)
                specularColor.rgb  *= vec3(lightMapColor.r);

        #elif defined(LIGHTMAP_G)
                specularColor.rgb  *= vec3(lightMapColor.g);

        #elif defined(LIGHTMAP_B)
                specularColor.rgb  *= vec3(lightMapColor.b);

        #elif defined(LIGHTMAP_A)
                specularColor.rgb  *= vec3(lightMapColor.a);
        #else
                specularColor.rgb  *= lightMapColor.rgb;

        #endif
    #endif

 #endif


#if defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING) && !defined(TOON)
    gl_FragColor.rgb =  AmbientSum2 * diffuseColor.rgb +
                    DiffuseSum.rgb * diffuseColor.rgb  * vec3(light.x) +
                    SpecularSum2.rgb * specularColor.rgb * vec3(light.y); 
       
#elif defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING) && defined(TOON)
        gl_FragColor.rgb =  (((AmbientSum2 * 0.35 + DiffuseSum.rgb) * diffuseColor.rgb)  +
                       SpecularSum2.rgb * specularColor.rgb * vec3(light.y));

    #elif !defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING)  && !defined(TOON)
                        gl_FragColor.rgb = AmbientSum2 * diffuseColor.rgb +
                                       DiffuseSum.rgb * diffuseColor.rgb  * vec3(light.x);

#elif !defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING) && defined(TOON)
        gl_FragColor.rgb =  (((AmbientSum2 * 0.35 + DiffuseSum.rgb) * diffuseColor.rgb));

#endif


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



    gl_FragColor.a = alpha;
}
