uniform sampler2D m_Texture;
uniform vec4 m_Color;

varying vec2 texCoord;

void main() {
    vec4 col = texture2D(m_Texture, texCoord);
    if (col.r <= 0.0) {
        discard;
    } else {
        col.a = m_Color.a * col.r;
        col.rgb = m_Color.rgb;
        
        gl_FragColor = col;
    }  
}

