varying vec2 vUv;

uniform sampler2D m_Texture;
uniform sampler2D m_MaskTexture;
uniform vec4 m_MaskColor;

// can also be uniform
const float partOf1 = 1.0f;

void main() {    
    
    vec4 texture = texture2D(m_Texture, vUv);
    vec4 textureMask = texture2D(m_MaskTexture, vUv);
    
    #ifdef HAS_MASK_COLOR
        textureMask *= m_MaskColor;
    #endif
    
    vec4 textureColor = texture * vec4(1.0f, 1.0f, 1.0f, partOf1) + textureMask * vec4(1.0f, 1.0f, 1.0f, 1.0f - partOf1);
    
    gl_FragColor = textureColor;   
    
    
}