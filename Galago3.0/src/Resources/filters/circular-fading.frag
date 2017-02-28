uniform float g_Aspect;
uniform vec2 g_Resolution;

uniform sampler2D m_Texture;
uniform vec3 m_CircleCenter;
uniform float m_CircleRadius;

varying vec2 texCoord;

void main() {
    vec3 color = texture2D(m_Texture, texCoord).rgb;
    
    float posX = (m_CircleCenter.x / g_Resolution.x) * g_Aspect;
    float posY = m_CircleCenter.y / g_Resolution.y;
    
    float d = distance(vec2(posX, posY), vec2(texCoord.x * g_Aspect, texCoord.y));
    float per = 1.0-pow(d, m_CircleRadius);
    //if (per > (1-m_CircleRadius)) {
    //    per = per * 2;
    //}
	
    //color.rgb *= step(d, m_CircleRadius);
    color.rgb *= per;

    gl_FragColor.rgb = color;
    gl_FragColor.a = 1.0;
}