uniform sampler2D m_Texture;
uniform vec4 m_Color;

varying vec2 texCoord;

void main(){
    gl_FragColor = vec4(m_Color.rgb, texture2D(m_Texture, texCoord).r * m_Color.a);
}