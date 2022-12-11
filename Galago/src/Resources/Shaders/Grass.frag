#ifdef TEXTURE
uniform sampler2D m_DiffuseMap;
varying vec2 texCoord;
#endif
 
uniform vec4 m_Color;

uniform float m_AlphaDiscardThreshold;
 
void main(void) {
    #ifdef TEXTURE
    vec4 texVal = texture2D(m_DiffuseMap, texCoord);
    if(texVal.a < m_AlphaDiscardThreshold){
        discard;
    }    
    gl_FragColor = texVal * m_Color;
    #else
    gl_FragColor = m_Color;
    #endif
}