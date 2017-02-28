#if defined(DISCARD_ALPHA)
    uniform float m_AlphaDiscardThreshold;
#endif

#ifdef TRANSLATE_UV 
  uniform vec2 m_TranslateAmount;
#endif

uniform float g_Time;
uniform vec2 g_Resolution;

uniform vec4 m_Color;
uniform sampler2D m_Texture;
uniform float m_GlowScale;
uniform float m_GlowThreshold;
uniform float m_GlowKernel;

varying vec2 texCoord1;

varying vec4 vertColor;

void main(){
    vec4 color = vec4(1.0);
    vec4 sum = vec4(0);

    //BEGIN --- This is for manipulating the text coord.
    vec2 calcTexCoord;
    #ifdef TRANSLATE_UV
       calcTexCoord = texCoord1 + (m_TranslateAmount* g_Time);
    #else
       calcTexCoord = texCoord1;
    #endif
    
    #ifdef HAS_TEXTURE
        #ifdef GLOW_ENABLED
            // mess of for loops due to gpu compiler/hardware limitations
            int j=-2;
            for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,calcTexCoord+vec2(i,j)*m_GlowKernel);
            j=-1;
            for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,calcTexCoord+vec2(i,j)*m_GlowKernel);
            j=0;
            for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,calcTexCoord+vec2(i,j)*m_GlowKernel);
            j=1;
            for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,calcTexCoord+vec2(i,j)*m_GlowKernel);
            j=2;
            for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,calcTexCoord+vec2(i,j)*m_GlowKernel);
            sum/=25.0;

            color = texture2D(m_Texture, calcTexCoord);    

        #else
            color *= texture2D(m_Texture, calcTexCoord);

        #endif

        

    #endif
    //END --- Make use of calcTexCoord

    #ifdef HAS_VERTEXCOLOR
        color *= vertColor;
    #endif

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    #if defined(DISCARD_ALPHA)
        if(color.a < m_AlphaDiscardThreshold){
           discard;
        }
    #endif


    gl_FragColor = color;

    #ifdef GLOW_ENABLED
        // use the blurred colour if it's bright enough
        if (length(sum)>m_GlowThreshold) {
            gl_FragColor +=sum*m_GlowScale;
        }
    #endif

    
}