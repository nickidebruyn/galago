#if defined(HAS_GLOWMAP) || defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

#if defined(DISCARD_ALPHA)
    uniform float m_AlphaDiscardThreshold;
#endif

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform sampler2D m_LightMap;

#ifdef TRANSLATE_UV 
  uniform vec2 m_TranslateAmount;
#endif

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

void main(){
    vec4 color = vec4(1.0);

    //BEGIN --- This is for manipulating the text coord.
    vec2 calcTexCoord;
    #ifdef TRANSLATE_UV
       calcTexCoord = texCoord1 + (m_TranslateAmount);
    #else
       calcTexCoord = texCoord1;
    #endif
    //END --- Make use of calcTexCoord

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, calcTexCoord);     
    #endif

    #ifdef HAS_VERTEXCOLOR
        color *= vertColor;
    #endif

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    #ifdef HAS_LIGHTMAP
        #ifdef SEPARATE_TEXCOORD
            color.rgb *= texture2D(m_LightMap, texCoord2).rgb;
        #else
            color.rgb *= texture2D(m_LightMap, calcTexCoord).rgb;
        #endif
    #endif

    #if defined(DISCARD_ALPHA)
        if(color.a < m_AlphaDiscardThreshold){
           discard;
        }
    #endif

    gl_FragColor = color;
}