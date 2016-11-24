uniform sampler2D m_Texture;
uniform vec2 m_DistortionPoint;
uniform float m_DistortionTime;
uniform vec3 m_ShockParams;

varying vec2 texCoord;

void main() {
      //vec4 texVal = texture2D(m_Texture, texCoord);
      //gl_FragColor = texVal * m_DistortionTime;

    vec2 uv = texCoord;
    vec2 tC = uv;
    float dist = distance(uv, m_DistortionPoint);
    if ((dist <= (m_DistortionTime + m_ShockParams.z)) && (dist >= (m_DistortionTime - m_ShockParams.z)) ) {
        float diff = (dist - m_DistortionTime); 
        float powDiff = 1.0 - pow(abs(diff*m_ShockParams.x), m_ShockParams.y); 
        float diffTime = diff  * powDiff; 
        vec2 diffUV = normalize(uv - m_DistortionPoint); 
        tC = uv + (diffUV * diffTime);
    } 
    gl_FragColor = texture2D(m_Texture, tC);
}