uniform sampler2D m_Texture;
uniform float m_Size;
uniform float m_Scale;
varying vec2 texCoord;



void main(void)
{  float blurSize = m_Scale/m_Size;
   vec4 sum = vec4(0.0);

   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y - 4.0*blurSize)) * 0.06;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y - 3.0*blurSize)) * 0.09;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y - 2.0*blurSize)) * 0.12;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y - blurSize)) * 0.15;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y)) * 0.16;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y + blurSize)) * 0.15;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y + 2.0*blurSize)) * 0.12;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y + 3.0*blurSize)) * 0.09;
   sum += texture2D(m_Texture, vec2(texCoord.x, texCoord.y + 4.0*blurSize)) * 0.06;

   gl_FragColor = sum;
}