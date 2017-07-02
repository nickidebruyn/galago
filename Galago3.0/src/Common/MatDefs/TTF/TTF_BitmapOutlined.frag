uniform sampler2D m_Texture;
uniform vec4 m_Color;
uniform vec4 m_Outline;

varying vec2 texCoord;

void main() {
    vec4 col = texture2D(m_Texture, texCoord);
    if (col.r <= 0.0) {
        discard;
    } else {
        col = ((m_Outline * col.r) * (1.0 - col.b)) + (m_Color * col.b);
        
        gl_FragColor = col;
    }  
}

