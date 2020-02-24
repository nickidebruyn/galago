#import "Common/ShaderLib/GLSLCompat.glsllib"
 
uniform sampler2D m_Texture;
varying vec2 texCoord;
uniform vec2 g_Resolution;
 
uniform int  m_BloomType;
uniform float  m_Strength;
uniform float  m_Size;
uniform int  m_Samples;

   
  
void main() {
          
    vec2 size = g_Resolution.xy;

    float uv_x = texCoord.x * size.x;
    float uv_y = texCoord.y * size.y;

    vec4 sum = vec4(0.0);
    for (int n = 0; n < m_Samples; ++n) {
        uv_y = (texCoord.y * size.y) + (m_Size * float(n - 4));
        vec4 h_sum = vec4(0.0);
        h_sum += texture2D(m_Texture, vec2(uv_x - (4.0 * m_Size), uv_y) /g_Resolution);
        h_sum += texture2D(m_Texture, vec2(uv_x - (3.0 * m_Size), uv_y)/g_Resolution );
        h_sum += texture2D(m_Texture, vec2(uv_x - (2.0 * m_Size), uv_y)/g_Resolution );
        h_sum += texture2D(m_Texture,  vec2(uv_x - m_Size, uv_y)/g_Resolution );
        h_sum += texture2D(m_Texture,  vec2(uv_x, uv_y) /g_Resolution);
        h_sum += texture2D(m_Texture,  vec2(uv_x + m_Size, uv_y) /g_Resolution);
        h_sum += texture2D(m_Texture, vec2(uv_x + (2.0 * m_Size), uv_y)/g_Resolution );
        h_sum += texture2D(m_Texture,  vec2(uv_x + (3.0 * m_Size), uv_y)/g_Resolution );
        h_sum += texture2D(m_Texture,  vec2(uv_x + (4.0 * m_Size), uv_y)/g_Resolution );
        sum += h_sum / float(m_Samples);
    }

    gl_FragColor = texture2D(m_Texture, texCoord) + ((sum / float(m_Samples)) * m_Strength);
     
}