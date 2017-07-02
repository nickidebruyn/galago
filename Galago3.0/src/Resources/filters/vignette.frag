uniform vec2 g_Resolution;
uniform sampler2D m_Texture;
uniform float m_Reduction;
uniform float m_Boost;

varying vec2 texCoord;

void main() {

    vec4 color = texture2D( m_Texture, texCoord );
    
    vec2 center = g_Resolution * 0.5;
    float vignette = distance(center, gl_FragCoord.xy ) / g_Resolution.x;
    vignette = m_Boost - vignette * m_Reduction;

    color.rgb *= vignette;
    gl_FragColor = color;
	
}